import numpy as np 
import random


class Grid_Mdp:

	def __init__(self):

		self.states = range(1, 9)

		# self.terminal_state = dict()
		# self.terminal_state[6] = 1
		# self.terminal_state[7] = 1
		# self.terminal_state[8] = 1

		self.terminal_states = [6, 7, 8]

		self.actions = ['n', 'e', 's', 'w']

		self.rewards = dict()
		self.rewards['1_s'] = -1.0
		self.rewards['3_s'] = 1.0
		self.rewards['5_s'] = -1.0

		self.t = dict()
		self.t['1_s'] = 6
		self.t['1_e'] = 2
		self.t['2_w'] = 1
		self.t['2_e'] = 3
		self.t['3_s'] = 7
		self.t['3_w'] = 2
		self.t['3_e'] = 4
		self.t['4_w'] = 3
		self.t['4_e'] = 5
		self.t['5_s'] = 8
		self.t['5_w'] = 4

		self.gamma = 0.8


	# @property
	# def terminal_states(self):
	# 	return self.terminal_states

	# @property
	# def gamma(self):
	# 	return self.gamma

	# @property
	# def states(self):
	# 	return self.states

	# @property
	# def actions(self):
	# 	return self.actions

		self.feas = dict()
		for i in range(1, 9):
			self.feas[i] = np.zeros([len(self.states),])
			self.feas[i][i-1] = 1
		# self.feas[1] = np.array([1,0,0,0,0,0,0,0])
		# self.feas[2] = np.array([0,1,0,0,0,0,0,0])
		# self.feas[3] = np.array([0,0,1,0,0,0,0,0])
		# self.feas[4] = np.array([0,0,0,1,0,0,0,0])
		# self.feas[5] = np.array([0,0,0,0,1,0,0,0])
		# self.feas[6] = np.array([0,0,0,0,0,1,0,0])
		# self.feas[7] = np.array([0,0,0,0,0,0,1,0])
		# self.feas[8] = np.array([0,0,0,0,0,0,0,1])


	# _ = self.start()

		self.current = random.randint(1, 5)

	def start(self, initial_state = None):

		# self.current_state = 1

		if None == initial_state:
			self.current = random.randint(1, 5)

		else:

			if initial_state in self.terminal_states:
				raise Exception('xx')

			self.current = initial_state

		return self.feas[ self.current ]


	def receive(self, action):

		state = self.current

		if state in self.terminal_states:
			return True, self.feas[state], 0

		key = '{}_{}'.format(state, action)
		
		if key in self.t.keys():
			self.current = self.t[key]
		else:
			self.current = state

		is_terminal = False
		if self.current in self.terminal_states:
			is_terminal = True

		if key not in self.rewards:
			r = 0.0
		else:
			r = self.rewards[key]

		return is_terminal, self.feas[ self.current ], r



	def transform(self, state, action):

		if state in self.terminal_states:
			return True, state, 0

		key = '{}_{}'.format(state, action)

		if key in self.t.keys():
			next_state = self.t[key]
		else:
			next_state = state

		is_terminal = False

		if next_state in self.terminal_states:
			is_terminal = True

		if key not in self.rewards.keys():
			r = 0.0
		else:
			r = self.rewards[key]

		return is_terminal, next_state, r



	def gen_randompi_sample(self, num):
		
		state_sample = []
		action_sample = []
		reward_sample = []

		for i in range(num):

			s_tmp = []
			a_tmp = []
			r_tmp = []

			s = self.states[ int(random.random()*len(self.states))]
			t = False
			while False== t:
				
				a = self.actions[ int(random.random()*len(self.actions))]
				t, s1, r = self.transform(s,a)
				s_tmp.append(s)
				r_tmp.append(r)
				a_tmp.append(a)
				s = s1

			state_sample.append(s_tmp)
			reward_sample.append(r_tmp)
			action_sample.append(a_tmp)


		return state_sample, action_sample, reward_sample




class Policy:

	def __init__(self, grid, epsilon):

		self.actions = grid.actions

		# grid.start()

		t, f, r = grid.receive(self.actions[0])	

		# self.theta = np.zeros([len(f)*len(self.actions), ])
		self.theta = np.random.randn(len(f)*len(self.actions))
		self.epsilon = epsilon

		# self.alpha = 1e-2

	def _get_fea_vec(self, fea, a):

		f = np.zeros_like(self.theta)

		idx = self.actions.index(a)

		for i in range(len(fea)):
			f[i+ idx*len(fea)] = fea[i]

		return f


	def qfunc(self, fea, a):

		f = self._get_fea_vec(fea, a)

		return np.dot(f, self.theta)


	def epsilon_greedy(self, fea):

		epsilon = self.epsilon

		amax = 0
		qmax = self.qfunc(fea, self.actions[0])

		for i in range(len(self.actions)):

			a = self.actions[i]
			q = self.qfunc(fea, a)
			if qmax< q:
				qmax = q
				amax = i

		pro = [ epsilon / len(self.actions) ] * len(self.actions)
		pro[amax] += (1-epsilon)

		return np.random.choice(self.actions, p=pro)


	def update(self, f,a,tvalue,alpha):

		pvalue = self.qfunc(f,a)
		# error = pvalue - tvalue
		# fea = self.get_fea_vec(f,a)
		self.theta -= alpha * (pvalue - tvalue) * self._get_fea_vec(f,a)



##########################################


class SoftmaxPolicy:

	def __init__(self, grid, epsilon):

		self.actions = grid.actions

		grid.start()

		t, hats, r = grid.receive(self.actions[0])

		self.theta = np.zeros([len(hats)* len(self.actions)]) + 0.01

		self.epsilon = epsilon



	def get_fea_vec(self, fea, a):

		f = np.zeros_like(self.theta)

		idx = self.actions.index(a)

		for i in range(len(fea)):
			f[i+ idx*len(fea)] = fea[i]

		return f



	def pi(self, fea):

		# prob = [ 0.0 for i in xrange(len(self.actions))]

		# for i in xrange(len(self.actions)):
		# 	f = self.get_fea_vec(fea,self.actions[i])
		# 	prob[i] = np.exp(np.dot(f, self.theta))

		prob = map(lambda x: np.exp(np.dot(self.get_fea_vec(fea, x), self.theta)), self.actions)

		prob = [p/sum(prob) for p in prob]

		return prob


	def take_action(self, fea):

		prob = self.pi(fea)

		# return self.actions[np.argmax(np.array(prob))]

		return np.random.choice(self.actions, p=prob)




class ValuePolicy:

	def __init__(self, grid, epsilon):

		self.actions = grid.actions

		grid.start()

		t, hats, r = grid.receive(self.actions[0])

		self.theta = np.array([0.0 for i in range(len(hats)* len(self.actions))])

		self.epsilon = epsilon


	def get_fea_vec(self, fea, a):

		f = np.zeros(self.theta.shape)

		idx = self.actions.index(a)

		f[idx*len(fea): (idx+1)*len(fea)] = fea

		return f


	def qfunc(self, fea, a):
		
		f = self.get_fea_vec(fea, a)
		return np.dot(f, self.theta)


	def epsilon_greedy(self, fea):

		epsilon = self.epsilon

		amax = 0
		qmax = self.qfunc(fea, self.actions[0])

		for i in range(len(self.actions)):

			a = self.actions[i]
			q = self.qfunc(fea, a)
			if qmax< q:
				qmax = q
				amax = i

		pro = [ epsilon / len(self.actions) ] * len(self.actions)
		pro[amax] += (1-epsilon)

		return np.random.choice(self.actions, p=pro)