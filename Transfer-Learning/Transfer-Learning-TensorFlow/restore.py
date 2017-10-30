import tensorflow as tf
import os

directories = os.listdir("flower_photos")
label2id = dict(zip(directories, range(len(directories))))
OUTPUT_TENSOR_NAME = 'output:0'
JPEG_DATA_TENSOR_NAME = 'DecodeJpeg/contents:0'

graph_def = tf.GraphDef()
with tf.gfile.FastGFile("output_graph_def.pb", "r") as f:
    graph_def.ParseFromString(f.read())
(output_tensor,
 jpeg_data_tensor) = tf.import_graph_def(graph_def=graph_def, name="", return_elements=[OUTPUT_TENSOR_NAME,
                                                                                        JPEG_DATA_TENSOR_NAME])
sess = tf.Session()
with tf.gfile.FastGFile("flower_photos/roses/118974357_0faa23cce9_n.jpg") as f:
    content = f.read()

prediction = sess.run(output_tensor, feed_dict={jpeg_data_tensor: content})