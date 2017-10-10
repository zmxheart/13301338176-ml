#!/usr/bin/env python
"""
    Artificial Intelligence for Humans
    Volume 3: Deep Learning and Neural Networks
    Python Version
    http://www.aifh.org
    http://www.jeffheaton.com
    Code repository:
    https://github.com/jeffheaton/aifh
    Copyright 2015 by Jeff Heaton
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    For more information on Heaton Research copyrights, licenses
    and trademarks visit:
    http://www.heatonresearch.com/copyright
"""

import numpy as np
from sklearn.manifold import TSNE
from lib.aifh.mnist import *
import pylab as Plot
import numpy as np
import matplotlib.pyplot as plt

X_train, y_train, X_val, y_val, X_test, y_test = load_dataset(False)

x = np.array(X_train[0:3000],dtype=np.float_)
yt = np.array(y_train[0:3000],dtype=np.int)

model = TSNE(n_components=2, random_state=0)
result = model.fit_transform(x)

x = result[:,0]
y = result[:,1]
#colors = y_train

plt.scatter(x, y, c=yt, cmap=plt.get_cmap("rainbow"))
plt.show()
