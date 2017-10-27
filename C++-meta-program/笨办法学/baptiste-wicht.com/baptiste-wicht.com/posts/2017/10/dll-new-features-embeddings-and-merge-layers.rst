I've just finished integrating new features into DLL, my deep learning library.
I've added support for an embeddings layer, a group layer and a merge layer.
This is not yet released, but available in the master branch.

Embeddings are used more and more these days to learn dense representation of
characters or word. An embedding layer in a neural network transform labels into
a vector. It's generally used as the first layer of the network. The embedding
are learned as part of the network.

The merge layer allows to create branches in the network. The input is passed to
each sub layer and then the output of each layer is concatenated to form the
output of the merged layers. This can be very useful to use different
convolutional filter sizes.

The group layer is a simple utility to group layers together. This is mostly to
use with merge layers to form several branches.

I've put together a new example to use these features on text classification.
The dataset is totally synthetic for now, but this can easily be reproduced with
a normal text classification dataset. This kind of model is called a Character
Convolutional Neural Network.

Here is the code for example:

.. code:: cpp

    constexpr size_t embedding = 16; // The length of the embedding vector
    constexpr size_t length = 15;    // The word (or sequence) length

    using embedding_network_t = dll::dyn_network_desc<
        dll::network_layers<
            // The embedding layer
            dll::embedding_layer<26, length, embedding>

            // The convolutional layers
            , dll::merge_layer<
                0
                , dll::group_layer<
                      dll::conv_layer<1, length, embedding, 16, 3, embedding>
                    , dll::mp_2d_layer<16, length - 3 + 1, 1, length - 3 + 1, 1>
                >
                , dll::group_layer<
                      dll::conv_layer<1, length, embedding, 16, 4, embedding>
                    , dll::mp_2d_layer<16, length - 4 + 1, 1, length - 4 + 1, 1>
                >
                , dll::group_layer<
                      dll::conv_layer<1, length, embedding, 16, 5, embedding>
                    , dll::mp_2d_layer<16, length - 5 + 1, 1, length - 5 + 1, 1>
                >
            >

            // The final softmax layer
            , dll::dense_layer<48, 10, dll::softmax>
        >
        , dll::updater<dll::updater_type::NADAM>     // Nesterov Adam (NADAM)
        , dll::batch_size<50>                        // The mini-batch size
        , dll::shuffle                               // Shuffle before each epoch
    >::network_t;

    auto net = std::make_unique<embedding_network_t>();

    // Display the network and dataset
    net->display();

    // Train the network for performance sake
    net->fine_tune(samples, labels, 50);

    // Test the network on train set
    net->evaluate(samples, labels);

The network starts with an embedding layer. The embedding is then passed to
three convolutional layers with different filter sizes, each followed by
a pooling layer. The outputs of the three layers are merged at the end of the
merge layer. Finally, a softmax layer is used for classification.

This kind of model can be very powerful and is used regularly. These new
features make for a much larger variety of models that can be build with the DLL
library.

The full code with the dataset generation can be found online:
`char_cnn.cpp <https://github.com/wichtounet/dll/blob/master/examples/src/char_cnn.cpp>`_

The next feature I want to focus on is recurrent neural networks. I'll probably
try a single RNN layer first and then upgrade to multi-layers and LSTM and maybe
GRU.
