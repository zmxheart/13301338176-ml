import numpy as np
import random

# load mnist images and labels
I = np.load("Images.npy")
L = np.load("Labels.npy")

# select a subsets of numbers for training, val and testing
max_number = 99
max_digits = len(str(max_number))
all_numbers = np.arange(max_number + 1)

P = np.random.rand(len(all_numbers))

# here the split happens
train_numbers = all_numbers[P < 0.7]
val_numbers = all_numbers[(P > 0.7) & (P < 0.85)]
test_numbers = all_numbers[P > 0.85]

def get_image(number):
    """

    :param number: an array of number indicies
    :return: image of the number
    """

    # get string representation of the number
    n = str(number)

    # trail number with zeros
    while len(n) < max_digits:
        n = "0" + n

    result = []

    # get image for every digit of the number
    for s in n:
        label = int(s)
        image = None
        # sample random indicies until one gets the right digit
        while image is None:
            i = np.random.randint(len(L))
            if L[i] == label:
                image = np.reshape( I[i, :], (28,28) ).T

        result.append(image)

    # images of all digits
    return np.column_stack(result)

learner_name = "Example"
predictor_name = "Some_input"

def load_batch(size, allowed_numbers):

    X = {learner_name:[], predictor_name:[]}
    Y = []

    for i in range(size):
        # select some number
        A = random.choice(allowed_numbers)
        B = A

        if i % 2 == 0:
            # generate number diferent than A every 2nd step
            while B == A:
                B = random.choice(allowed_numbers)

        # remeber generate images
        E_image = get_image(A)
        S_image = get_image(B)

        X[learner_name].append(E_image)
        X[predictor_name].append(S_image)
        Y.append(A == B)

    # convert to format supported by keras
    for key in X:
        X[key] = np.array(X[key]).astype('float32')
        X[key] = X[key][:,np.newaxis, :, :]

    Y = np.array(Y).astype('int32')

    return X, Y

