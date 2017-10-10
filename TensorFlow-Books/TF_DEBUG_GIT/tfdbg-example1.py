import numpy as np
import tensorflow as tf
import tensorflow.python.debug as tf_debug
xs = np.linspace(-0.5, 0.49, 100)
x = tf.placeholder(tf.float32, shape=[None], name="x")
y = tf.placeholder(tf.float32, shape=[None], name="y")
k = tf.Variable([0.0], name="k")
y_hat = tf.multiply(k, x, name="y_hat")
sse = tf.reduce_sum((y - y_hat) * (y - y_hat), name="sse")
train_op = tf.train.GradientDescentOptimizer(learning_rate=0.02).minimize(sse)

sess = tf.Session()
sess.run(tf.global_variables_initializer())

sess = tf_debug.LocalCLIDebugWrapperSession(sess)
for _ in range(10):
  sess.run(train_op, feed_dict={x: xs, y: 42 * xs})
