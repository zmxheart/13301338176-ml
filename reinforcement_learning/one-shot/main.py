import data_loader as dl # configure constants in this package to adjust data

from keras.layers import Input, Dense, merge, Convolution2D, Flatten, Embedding
from keras.models import Model
from keras.optimizers import Adam

# Network which learns by example
learner_input = Input(shape=(1, 28, 28 * dl.max_digits), dtype='float32', name=dl.learner_name)
x = learner_input

for i in range(3):
    x = Convolution2D(16,3,3)(x)

x = Flatten()(x)
x = Dense(128, activation='relu')(x)

# code of size 32
learned_code = Dense(32)(x)


# Network which does prediction given learned code
prediction_input = Input(shape=(1,28,28*dl.max_digits), dtype='float32', name=dl.predictor_name)
x = prediction_input

for i in range(3):
    x = Convolution2D(16,3,3)(x)


x = Flatten()(x)
x = Dense(128, activation='relu')(x)

# concatenate code from example with features of image for which prediction is made
x = merge([x, learned_code], mode='concat')

x = Dense(128, activation='relu')(x)

# prediction: currently binary possibility (whether input given to predictor is of same class as example)
network_prediction = Dense(2, activation='softmax')(x)


model = Model(input=[learner_input, prediction_input], output=network_prediction)

model.compile(optimizer=Adam(lr=0.0001),
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

# generate data ...

print "Generating data"

x,y = dl.load_batch(2 ** 16, dl.train_numbers)
xv, yv = dl.load_batch(2 ** 12, dl.val_numbers)
xt, yt = dl.load_batch(2 ** 12, dl.test_numbers)

# do the actual training

for i in range(100):
    model.fit(x,y, batch_size=128, nb_epoch=1, validation_data=(xv, yv))
    print 'test error:', model.evaluate(xt, yt)