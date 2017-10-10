import tensorflow as tf

# Create some variables.
v1 = tf.Variable(1)
v2 = tf.Variable(2)

# Add an op to initialize the variables.
init_op = tf.initialize_all_variables()

# Add ops to save and restore all the variables.
saver = tf.train.Saver()

# Later, launch the model, initialize the variables, do some work, save the
# variables to disk.
with tf.Session() as sess:
  sess.run(init_op)
  print "v1 = ", v1.eval()
  print "v2 = ", v2.eval()
  # Save the variables to disk.
  save_path = saver.save(sess, "/tmp/model3.ckpt")
  print "Model saved in file: ", save_path
