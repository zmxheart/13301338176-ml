from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
import tensorflow as tf
import os

BOTTLENECK_TENSOR_NAME = 'pool_3/_reshape:0'


def produce_input_queues(filename):
    tfrecords_file_path = os.path.join("tfrecords", filename)
    queue = tf.train.input_producer([tfrecords_file_path])
    reader = tf.TFRecordReader()
    _, serialized = reader.read(queue=queue)
    features = tf.parse_single_example(serialized=serialized,
                                       features={
                                           "bottleneck_tensor_value": tf.FixedLenFeature([], tf.string),
                                           "label": tf.FixedLenFeature([], tf.int64)
                                       })
    bottleneck_tensor_value = tf.decode_raw(features['bottleneck_tensor_value'], tf.float32)
    bottleneck_tensor_value.set_shape([2048])
    label = features['label']
    bottleneck_tensor_values, labels = tf.train.shuffle_batch(tensors=[bottleneck_tensor_value, label],
                                                              batch_size=64,
                                                              capacity=2000 + 60 * 3,
                                                              min_after_dequeue=2000)
    return bottleneck_tensor_values, labels


def train_final_layer():
    bottleneck_tensor_values, labels = produce_input_queues("train.tfrecords")
    valid_tensor, valid_labels = produce_input_queues("valid.tfrecords")
    with tf.variable_scope("inferece") as scope:
        softmax_w = tf.get_variable(name="softmax_w",
                                    shape=[2048, 5],
                                    initializer=tf.truncated_normal_initializer(stddev=0.001),
                                    dtype=tf.float32)
        softmax_b = tf.get_variable(name="softmax_b",
                                    shape=[5],
                                    initializer=tf.constant_initializer(value=0.),
                                    dtype=tf.float32)
        logits = tf.nn.xw_plus_b(x=bottleneck_tensor_values,
                                 weights=softmax_w,
                                 biases=softmax_b)
        valid_logits = tf.nn.xw_plus_b(valid_tensor, softmax_w, softmax_b)

    prediction = tf.argmax(valid_logits, axis=1)
    equal = tf.cast(tf.equal(prediction, valid_labels), tf.float32)
    accuracy = tf.reduce_mean(equal)
    tf.summary.scalar(name="accuracy", tensor=accuracy)
    loss_per_example = tf.nn.sparse_softmax_cross_entropy_with_logits(logits=logits,
                                                                      labels=labels)
    loss = tf.reduce_mean(loss_per_example)
    tf.summary.scalar(name="loss", tensor=loss)
    optimizer = tf.train.GradientDescentOptimizer(learning_rate=0.01)
    train_op = optimizer.minimize(loss)
    summary_writer = tf.summary.FileWriter("log")
    merged = tf.summary.merge_all()
    init = tf.group(tf.global_variables_initializer(), tf.local_variables_initializer())
    with tf.Session() as sess:
        sess.run(init)
        tf.train.start_queue_runners(sess=sess)
        for index in range(2000):
            _, loss_value, summary = sess.run([train_op, loss, merged])
            accuracy_value = sess.run(accuracy)
            summary_writer.add_summary(summary, global_step=index)
            if (index + 1) % 100 == 0:
                print("step: %d loss: %f accuracy: %f\n" % (index + 1, loss_value, accuracy_value))
        softmax_w_values, softmax_b_values = sess.run([softmax_w, softmax_b])
    graph_def = tf.GraphDef()
    with tf.gfile.FastGFile("model_dir/classify_image_graph_def.pb", "r") as f:
        graph_def.ParseFromString(f.read())
    [bottleneck_tensor] = tf.import_graph_def(graph_def=graph_def, name="", return_elements=[BOTTLENECK_TENSOR_NAME])
    softmax_w_constant = tf.constant(softmax_w_values)
    softmax_b_constant = tf.constant(softmax_b_values)
    output = tf.nn.xw_plus_b(bottleneck_tensor, softmax_w_constant, softmax_b_constant)
    softmax = tf.nn.softmax(output, name="output")
    summary_writer.add_graph(graph=tf.get_default_graph())
    output_graph_def = tf.graph_util.extract_sub_graph(tf.get_default_graph().as_graph_def(), dest_nodes=["output"])
    with tf.gfile.FastGFile("output_graph_def.pb", "wb") as f:
        f.write(output_graph_def.SerializeToString())


if __name__ == "__main__":
    train_final_layer()

