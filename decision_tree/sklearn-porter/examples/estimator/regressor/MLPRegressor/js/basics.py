# -*- coding: utf-8 -*-

from sklearn.neural_network import MLPRegressor
from sklearn.datasets import load_diabetes
from sklearn_porter import Porter


samples = load_diabetes()
X, y = samples.data, samples.target

reg = MLPRegressor(
    activation='logistic', hidden_layer_sizes=[30, 20, 30], max_iter=500, alpha=1e-4,
    solver='sgd', tol=1e-4, random_state=1, learning_rate_init=.1)
reg.fit(X, y)

porter = Porter(reg, language='js')
output = porter.export()
print(output)

"""
var MLPRegressor = function(hidden, layers, weights, bias) {

    this.hidden = hidden.toUpperCase();
    this.network = new Array(layers.length + 1);
    for (var i = 0, l = layers.length; i < l; i++) {
        this.network[i + 1] = new Array(layers[i]).fill(0.);
    }
    this.weights = weights;
    this.bias = bias;

    var compute = function(activation, v, nLayers) {
        switch (activation) {
            case 'LOGISTIC':
                if (nLayers > 1) {
                    for (var i = 0, l = v.length; i < l; i++) {
                        v[i] = 1. / (1. + Math.exp(-v[i]));
                    }
                } else {
                    for (var i = 0, l = v.length; i < l; i++) {
                        if (v[i] > 0) {
                            v[i] = -Math.log(1. + Math.exp(-v[i]));
                        } else {
                            v[i] = v[i] - Math.log(1. + Math.exp(-v[i]));
                        }
                    }
                }
                break;
            case 'RELU':
                for (var i = 0, l = v.length; i < l; i++) {
                    v[i] = Math.max(0, v[i]);
                }
                break;
            case 'TANH':
                for (var i = 0, l = v.length; i < l; i++) {
                    v[i] = Math.tanh(v[i]);
                }
                break;
            case 'SOFTMAX':
                var max = Number.NEGATIVE_INFINITY;
                for (var i = 0, l = v.length; i < l; i++) {
                    if (v[i] > max) {
                        max = v[i];
                    }
                }
                for (var i = 0, l = v.length; i < l; i++) {
                    v[i] = Math.exp(v[i] - max);
                }
                var sum = 0.0;
                for (var i = 0, l = v.length; i < l; i++) {
                    sum += v[i];
                }
                for (var i = 0, l = v.length; i < l; i++) {
                    v[i] /= sum;
                }
                break;
        }
        return v;
    };

    this.predict = function(neurons) {
        this.network[0] = neurons;
    
        for (var i = 0; i < this.network.length - 1; i++) {
            for (var j = 0; j < this.network[i + 1].length; j++) {
                for (var l = 0; l < this.network[i].length; l++) {
                    this.network[i + 1][j] += this.network[i][l] * this.weights[i][l][j];
                }
                this.network[i + 1][j] += this.bias[i][j];
            }
            if ((i + 1) < (this.network.length - 1)) {
                this.network[i + 1] = compute(this.hidden, this.network[i + 1], this.network.length);
            }
        }
    
        if (this.network[this.network.length - 1].length > 1) {
            return this.network[this.network.length - 1];
        }
        return this.network[this.network.length - 1][0];
    };

};

if (typeof process !== 'undefined' && typeof process.argv !== 'undefined') {
    if (process.argv.length - 2 === 10) {

        // Features:
        var features = process.argv.slice(2);

        // Parameters:
        const layers = [30, 20, 30, 1];
        const weights = [[[-0.0365879807229, 0.0811693325814, -0.224866025174, -0.0691157162294, -0.159307192507, -0.18421882645, -0.132468024064, -0.0540899399421, -0.0451907204648, 0.0178294565109, -0.0271046000869, 0.0731882701634, -0.139199466881, 0.190702304676, -0.207242718779, 0.0941816990103, -0.0233188049172, 0.0143506790547, -0.163903508369, -0.124129513915, 0.145311306631, 0.207270733621, -0.0675581103436, 0.0753216562148, 0.160845705887, 0.184805699455, -0.1806338449, -0.213899834603, -0.134750289845, 0.168700066053], [-0.177494443809, -0.0412908205627, 0.204233263726, 0.0227933224387, 0.0855406019533, -0.0831000388635, 0.0861037398838, 0.154386169809, -0.215626217835, 0.112029284267, 0.221481990938, 0.109296052507, -0.0986643198093, 0.138869427295, -0.173894555695, -0.0172612316225, 0.188591667716, -0.0949326848035, -0.0932745574602, -0.161134198786, -0.211228662626, 0.0804967516486, -0.122114595895, -0.106969734437, -0.00394234337737, -0.19536848496, 0.0342824128094, -0.158229558891, 0.0444935265336, 0.0894821848837], [-0.184018829726, -0.0896917026173, 0.0824012438978, 0.0116672769453, -0.206280365148, 0.0105842613046, 0.0963774237417, 0.0506104813481, 0.202104881782, 0.0357572302855, 0.206449222516, -0.194878250953, -0.188223070329, 0.196917636046, -0.0412344187674, -0.0971045988209, 0.228194140053, -0.108959727315, 0.0963303278844, 0.131314342011, 0.20447869966, 0.0443496862829, 0.155833467536, -0.106690368068, -0.131923129847, 0.193970770829, -0.0155500778462, 0.177610331364, 0.109412555077, 0.0515167110687], [-0.177813303562, 0.153236766154, -0.0266314554638, 0.0817998950553, -0.0457039028855, -0.122678969151, 0.201903201185, 0.0738062616281, -0.219350173516, 0.0496632812981, -0.0533832683326, -0.0181994670857, 0.147823777585, -0.00802307117768, 0.187127092071, 0.104077726844, -0.181904509436, 0.154196442785, 0.071004444671, 0.250651010063, -0.115783406257, -0.172338585071, 0.234202611771, 0.0518693570887, -0.220819076554, 0.130224495538, 0.128917860542, 0.161331514839, 0.128424233281, -0.170697303131], [-0.216546844354, -0.235159331396, -0.213040558357, -0.0899064158793, 0.158822089246, 0.0148974773476, 0.0341560552147, 0.172649447034, -0.16686249475, -0.100025267971, 0.0499867213196, 0.195982069914, 0.0161039850446, -0.187233709372, 0.137566291384, -0.0956486016717, 0.154704092489, -0.06793679163, 0.156692891114, 0.124531842555, 0.0400542201953, -0.166860193348, -0.176376633076, -0.18624683178, -0.215672915867, -0.167015811857, -0.115427111811, 0.0827681407613, 0.0432368289984, -0.219151239053], [-0.19039520846, 0.187464755708, 0.0285986906209, -0.108778133755, -0.112486375294, 0.106780446807, -0.126450683848, 0.0544055616429, 0.210866130905, 0.154684059211, -0.10567262947, -0.0139564016881, 0.0456744142771, 0.175544765076, -0.147913942731, -0.193315289501, -0.174845162886, -0.0206094917338, 0.0450688761208, 0.0445100834649, -0.0681182556256, 0.216183152368, 0.0560539995142, -0.0668880191654, 0.0144990968007, 0.119908349662, 0.0816228208865, -0.113704413599, -0.178218609544, -0.0586762486018], [0.0523227168744, -0.0928094717221, 0.115588546972, -0.238493091502, -0.104917924701, 0.140197821639, -0.153869111371, 0.0306687150003, 0.0096265094849, 0.187649164546, -0.124943405475, -0.176609769452, 0.116179008834, 0.0711195840393, 0.169316606459, 0.154862985595, -0.248676367184, -0.0966627669113, 0.05398265652, 0.176288214087, 0.178584893891, 0.027252814514, 0.149640335967, 0.0822893688212, -0.0378452487648, -0.0279933628278, 0.0373127575654, 0.033689064952, 0.16228374329, 0.187246660262], [-0.0420009753643, 0.166433660998, -0.148863516348, -0.118871572268, -0.165942768647, -0.00176793626898, -0.195407415116, 0.235128761042, 0.147694995794, -0.215450782071, -0.124028588136, -0.0949155613257, -0.178065129713, 0.193935742967, -0.0561136138287, 0.23901953649, 0.0709166936766, 0.144005773372, 0.151536789403, 0.208161875118, 0.00742561129478, 0.0179322180451, 0.173282324755, -0.118122437309, -0.0177732089897, 0.0669394341146, -0.206051574517, 0.0280671383712, 0.00121575525382, 0.137127309623], [-0.0841372775407, 0.129759018365, 0.0312524659596, -0.0924608903182, 0.124942572492, 0.0451713219259, -0.178653949785, 0.00411543176588, 0.0830382722151, 0.187256772629, -0.199573538298, 0.185942960446, -0.0762751935276, 0.267338518042, 0.054719648163, 0.194625686697, 0.0681797329988, 0.0235927145729, -0.107027663058, 0.0669920001755, 0.140870093607, 0.152780099298, 0.154657034021, 0.0574166590018, 0.140281984466, -0.0597671198126, 0.0903252729636, -0.0454411872926, -0.0190100304172, -0.04150972512], [-0.0602910258769, -0.118609395591, 0.0505466128583, -0.00514546957334, 0.206883224757, 0.0755233188925, -0.118425913578, -0.00126928197136, -0.0663050730782, 0.126497228752, 0.188541385615, 0.149984449943, 0.0436887118669, -0.0701339618398, -0.11858925474, 0.195780577519, 0.0331455334075, 0.0977334120063, 0.00936218441522, 0.122481799278, 0.033262037573, 0.106447611529, 0.0560769194298, -0.05373812051, -0.102456383497, -0.191598934537, -0.0396299290855, -0.221978134732, 0.239451746227, -0.146824757509]], [[2.13614984851, -4.20547546755, 4.91125737103, 7.50423314674, -3.03759500086, 3.74706537753, 7.73259459977, 3.59122215444, 11.2203400417, 15.6032187324, -1.59714234035, -6.76548097912, 12.3324286363, -3.2817064398, 6.25512904932, 0.465887306247, 3.03136004948, 3.44919401982, 4.99901779458, -1.58726411641], [3.2994579304, -4.24584097584, 4.95760668244, 7.46144506015, -3.11715904786, 3.91740395694, 10.7177533203, 3.44925519272, 11.2829699333, 15.9086270002, -1.80373347008, -7.1462020669, 12.4615811225, -3.43165447489, 6.6542377206, 0.114390237269, 3.16050332276, 3.4489639098, 4.92538809778, -1.67974043564], [2.35327934242, -4.39219123177, 4.76565750256, 7.41505214287, -3.26315240683, 3.79089356997, 8.33366375288, 3.43667547593, 10.8929421855, 15.0893952199, -1.71831184417, -6.74635211946, 12.1051783491, -3.45949286344, 6.16069923615, 0.228387245902, 2.97825192806, 3.30606031635, 4.58475045852, -1.38818641964], [1.26239218714, -3.97468174109, 4.69366312557, 6.93891568832, -2.96126441793, 3.88264598098, 4.95626027017, 3.37262258076, 10.541642468, 14.8373894581, -1.50262207289, -6.48235723588, 11.4146650966, -3.17124046076, 6.08583082412, 0.864594387914, 2.69503737617, 3.14457184136, 4.47151759483, -1.26204769645], [1.97712991514, -3.66102670635, 4.45125694082, 6.54745475895, -2.61073252613, 3.33839352798, 7.38214533135, 2.9417260395, 9.69102825959, 13.4996880986, -1.64051205992, -6.16203270584, 10.3356371026, -3.12610022589, 5.517846443, 0.128152752777, 2.35282528561, 3.0392449834, 4.16641964217, -1.13452665366], [2.27641613196, -3.99672746156, 4.62604948494, 6.91578347136, -2.78736846561, 3.60401341239, 7.95104294779, 2.99508723632, 10.3465021638, 14.2497640947, -1.55572581958, -6.61308492845, 11.2879960932, -3.35993915157, 6.1056473261, 0.459651252237, 2.83861755115, 3.31076839782, 4.3636250517, -1.45999142736], [1.65757129259, -3.81792294874, 4.66933773203, 6.93064570497, -2.74524769918, 3.63466958198, 6.36157060772, 3.05259699554, 9.85404565105, 13.870902632, -1.72735461302, -6.11475019534, 10.8196608255, -3.16825602188, 5.8891428546, 0.385222294653, 2.83960079922, 2.96460113036, 4.39446052393, -1.3037630836], [1.62647685328, -3.82671326634, 4.37192368205, 6.5761520695, -2.58666524207, 3.26789973348, 5.28607002396, 3.17356996918, 9.52374298593, 13.6708916648, -1.61665408726, -5.94457794998, 10.7184803183, -2.92360440145, 5.60315418083, 0.607359634682, 2.49982564939, 2.78776119542, 4.30578402108, -1.23965499029], [2.3048876027, -3.95700740576, 4.77933285284, 7.07843797391, -3.06890488206, 3.94552788296, 8.00179973817, 3.4342620355, 10.7812528425, 14.8191888486, -1.83054626985, -6.76082478457, 11.611348731, -3.40822007203, 6.18657384122, 0.308128187286, 2.9502359924, 3.13478502254, 4.75300925928, -1.46145583688], [2.26072430165, -3.98088376335, 4.40040193747, 6.71252960199, -2.80542247136, 3.57616290086, 7.59500824139, 3.00180600371, 10.1153605672, 14.3853339579, -1.4556689775, -6.28159314355, 11.2941700911, -2.95656495121, 5.87908318518, 0.127093177787, 2.52759621182, 2.92619141349, 4.42384107406, -1.33831963721], [2.2657712464, -4.54175840478, 5.33093265669, 7.7831097885, -3.09775438877, 4.08648023579, 7.26871757637, 3.5152783841, 11.1528407074, 15.9021005706, -1.79780466308, -7.01989327414, 12.4815222467, -3.52216496437, 6.42397503492, 0.316516312254, 3.12375182736, 3.41450515586, 5.18941801399, -1.51504376333], [2.72645645622, -4.3159659559, 5.13623085902, 7.5251422407, -3.30995588553, 3.89968191875, 9.36933424726, 3.53157139404, 11.2469745409, 15.522088226, -1.92321151547, -6.97841451324, 12.3928823234, -3.63259365564, 6.51415457075, 0.279082269906, 2.93052056015, 3.58635519186, 4.95702821973, -1.68408996131], [2.33579461878, -3.62721144549, 4.52554266822, 6.62864902482, -2.68750083273, 3.48843240741, 7.80092196789, 3.04049060499, 9.776732497, 13.5968503067, -1.37172070082, -6.20835992309, 10.789694969, -3.08150586593, 5.72812045102, 0.188807500088, 2.70054536751, 2.92328321861, 4.15738728958, -1.17066066171], [0.865244010742, -3.44433221877, 4.2061532173, 6.06235621088, -2.69882480683, 3.14528251357, 3.40633953168, 2.78554548331, 8.92587701966, 12.6695621315, -1.39462696154, -5.60110031217, 9.87604967681, -2.91829976648, 5.15468348094, 0.836301018389, 2.49278699604, 2.81951349831, 3.92706358264, -0.925207208847], [1.78580963542, -3.55003370286, 4.41605924329, 6.47016931675, -2.87040541998, 3.59960349612, 6.12192936904, 2.85588663102, 9.54766000642, 13.4306035359, -1.65271707433, -5.91118854582, 10.4198223948, -2.89899325146, 5.64353969809, 0.289463333192, 2.58438073398, 3.04085224046, 4.26885763802, -1.15803255647], [1.35358205851, -3.91352844194, 4.84634911349, 6.94827968173, -2.81944004571, 3.49381662454, 5.29492733131, 3.08083822288, 10.2711351876, 14.4268380463, -1.81457644211, -6.30352674305, 11.3048080133, -3.17238158394, 5.96662753599, 0.486198923274, 2.62453562484, 2.99868127748, 4.54170288768, -1.31894575874], [1.57854680612, -4.31638221595, 4.95608339013, 7.0931295994, -2.84739219519, 3.69318794248, 5.82788735353, 3.1470493726, 10.826579965, 14.9448383223, -1.69266385534, -6.62212219716, 11.6584023813, -3.28561907304, 6.08458739161, 0.69985317176, 2.92970812886, 3.16021313397, 4.85614119472, -1.48936018998], [2.76168008692, -3.71581850957, 4.3167693811, 6.61851504651, -2.64577025743, 3.62514728919, 8.63910361327, 2.79309044373, 9.80103804396, 13.686023629, -1.39902676606, -6.02980455523, 10.685378644, -2.85986490319, 5.52157913933, 0.210240245574, 2.65408440476, 3.00210938573, 4.19675087735, -1.22681832278], [1.87072061143, -3.57991716301, 4.44811853682, 6.331516767, -2.84609642081, 3.28688557426, 6.95494838882, 3.10835718578, 9.52219598162, 13.2871496722, -1.63326227058, -6.04744544939, 10.51683234, -3.00243629365, 5.48127163268, 0.265924688159, 2.64452805359, 2.88850439314, 4.11669056137, -1.16545168076], [1.75754060092, -4.29609616065, 4.91183716764, 7.51513676314, -2.98099797192, 3.91240467677, 6.6512854059, 3.4101047758, 11.0911119694, 15.3672233984, -1.77464222884, -6.75251606297, 12.1890217015, -3.2974667629, 6.38362550844, 0.54394842254, 3.06280225938, 3.21420271691, 4.88057530627, -1.18002085281], [1.67761289708, -4.02750342203, 4.68719387841, 6.70528698754, -2.77630239066, 3.59951008369, 6.03884300412, 3.17409879746, 10.1901951251, 13.9475849345, -1.39936760365, -6.4737076892, 11.0025773562, -2.92568905723, 5.62241947519, 0.640310112447, 2.81401323272, 3.06430229837, 4.32607423398, -1.10960362867], [2.34041965911, -4.24120639496, 4.94065133855, 7.29301525169, -2.99718192647, 3.8993525355, 7.94147111063, 3.34939289077, 10.5022010207, 14.8330442178, -1.77514826715, -6.42460131686, 11.5286274342, -3.41341152747, 5.95911232139, 0.114505308082, 2.88433717631, 3.26395013601, 4.75660786928, -1.36215561213], [1.4164653554, -3.61805095916, 4.40988562364, 6.40819970451, -2.64297687576, 3.27365825566, 4.60865673926, 2.82855351978, 9.48402903419, 13.290621312, -1.55659250887, -5.78241745856, 10.415879043, -2.7446002015, 5.56238516167, 0.535302949179, 2.66457442141, 2.97114522342, 4.08683396536, -1.02061996807], [2.48605237301, -3.73854643527, 4.22591113223, 6.48836918337, -2.78541065734, 3.49070180173, 8.31880088798, 2.96067398643, 9.47040828184, 13.4694402223, -1.31506199591, -6.06452638354, 10.4665357496, -2.76738350892, 5.46360964307, 0.299625732266, 2.43186061662, 2.95457808965, 4.24447488099, -1.37809071652], [2.25024873587, -4.01132194726, 4.80668562812, 6.8698334431, -2.795117361, 3.5226504646, 8.17943606951, 3.04112367537, 10.3298849029, 14.4891790416, -1.52238056065, -6.52215054859, 11.358564748, -3.27071764283, 5.89714501715, 0.184892439245, 2.65776297544, 2.96873553231, 4.55860286794, -1.26636472353], [1.87822028295, -3.95311507515, 4.54630326082, 6.84642663762, -2.93127615144, 3.82252160157, 6.25439541684, 3.19305892228, 10.2991988953, 14.5254651075, -1.77056699698, -6.53616908784, 11.2762666584, -3.05199374029, 6.09833619893, 0.457410736935, 2.79076198541, 3.03117550426, 4.55002125808, -1.35254925726], [1.99708884186, -3.6369083275, 4.37334396788, 6.30264033929, -2.72338173802, 3.58702889688, 6.56357806435, 3.02084591223, 9.39875518563, 13.4717472569, -1.47799380634, -5.99104888102, 10.4735872242, -2.90604294352, 5.66099863997, 0.487805993987, 2.56853103188, 3.1001492009, 4.02424087331, -0.998536842567], [2.34423029194, -4.18895585921, 4.70501598725, 6.93460313702, -2.92340295764, 3.55681898522, 8.41286148077, 3.08069195866, 10.5588281293, 14.8065868872, -1.68190148378, -6.69554583038, 11.638808084, -3.29750441973, 6.18072670604, 0.227142256765, 2.78689694206, 3.02882583125, 4.66621075381, -1.4439809961], [1.57261478077, -4.08480219772, 4.68403118002, 7.05518266425, -2.77890726755, 3.66148869585, 6.03394446058, 3.26603097087, 10.6019368172, 14.6356293052, -1.76186211604, -6.38349985003, 11.3199289776, -3.04598012156, 6.06922153482, 0.507726966026, 2.97709717508, 3.03272144366, 4.60470027428, -1.45278307355], [2.35181232534, -4.29521058558, 4.91240872951, 7.44802721997, -2.89960297987, 3.79334989266, 7.99661364557, 3.44250639293, 10.7238917039, 15.0979839361, -1.8229251506, -6.7745914857, 11.666569411, -3.2222322563, 6.19983732272, 0.325754854775, 3.00825523512, 3.30643962292, 4.68558824832, -1.46436297902]], [[-73.0084260622, -130.078894221, -3.0100117742, -46.3938985881, -20.1878718975, -4.09668581429, 2.53053164123, 2.85025318051, 2.9572163613, -5.1327085164, -70.159164482, -4.85972714201, -18.7133529526, -22.4905403095, -110.504588648, -104.23652711, 2.04071843934, -6.90128416386, -40.634898319, 2.8379345955, -3.31271086144, -205.135246032, -39.4750721186, 1.9326916265, -3.27367337486, 1.78341776165, -41.7632433206, 2.42037705452, -3.27615515004, -2.82321539113], [-73.5489843031, -131.890466382, -3.76548794146, -47.1672952548, -19.8239820453, -4.72962208679, 3.50656006815, 3.8772356034, 4.38048881665, -4.61641909153, -71.1981768138, -4.79989691777, -19.5042249204, -23.4194046881, -111.210906725, -104.719794532, 2.92130470596, -7.46599220312, -1.84853420396, 4.09742860213, -4.69670007658, -207.338086241, -39.3960613682, 2.77711086709, -4.02172840782, 2.67175643229, -42.6568967523, 3.3375002585, -4.57438615051, -3.89248050621], [-16.6408739263, -32.5824739515, -4.21908111771, -12.4028186268, -3.58781246803, -3.80047621011, 3.95802830871, 4.55821382195, 4.62828576319, 1.435459676, -17.9752904354, 2.50269865174, -6.44242475041, -6.99311274403, -26.1141044474, -24.755459591, 3.86200006422, -3.7662585597, -244.825220148, 4.75284683035, -5.3844059152, -50.4150341934, -8.52277224776, 3.42790656231, -3.41021496399, 3.39634539798, -11.3823161513, 3.85066870945, -4.80612113014, -4.21149666243], [-70.3952971848, -125.664459403, -3.39480481065, -44.6092532557, -18.8849565842, -4.1663373777, 3.10100976223, 3.32123515712, 3.69985669485, -4.02945277594, -68.0962356078, -2.73647108561, -18.3329320837, -22.2741080376, -106.143255735, -100.175231826, 2.50947139116, -6.83454901778, -248.697359294, 3.3719524899, -3.75337744221, -198.240158975, -38.010804993, 2.31398250371, -3.43662212508, 2.29252811249, -40.5021375017, 2.75398433481, -3.53359534494, -3.3748326206], [-100.98670033, -180.518901506, -5.18392004297, -64.3487817153, -27.1089043641, -6.30840388253, 4.54511769177, 5.23514851019, 5.51414952983, -6.59689976794, -97.8818473584, -6.4732335784, -26.5967041378, -32.0653105014, -152.801080157, -143.978029206, 3.73827577872, -10.3032231559, -2.80319674244, 5.28436355977, -5.93978051801, -284.499715541, -54.3625341332, 3.45089073233, -5.22860511948, 3.61058387508, -58.3539907262, 4.49116318252, -5.49599200618, -5.12813865767], [-111.100293353, -197.615005057, -4.19635918215, -70.2672705733, -30.3471993416, -5.72256296442, 3.59498072051, 4.09405903721, 4.38053071706, -7.23121603612, -106.926220044, -6.40928420364, -28.3144730342, -34.3196977434, -167.880779982, -158.188840464, 2.6542254859, -10.3299037315, -250.354978623, 3.78898795267, -4.75771193231, -312.168878857, -59.8715966306, 2.47705937101, -4.66533669149, 2.750685591, -63.2592877098, 3.33626939799, -4.41559377393, -4.17411436505], [-106.404348282, -189.897969125, -4.49087291874, -67.4990459636, -28.9791787308, -6.10507404416, 4.02888789473, 4.58351954469, 4.81860439768, -7.30368331109, -102.82724872, -7.15411784034, -27.5540771789, -33.0837750823, -160.848450155, -151.622570905, 3.28426073918, -10.0457139498, -55.0522364666, 4.27125925337, -5.05122979375, -299.289327365, -57.2874439808, 2.69991611941, -4.9507386678, 2.90241511658, -60.9586967252, 3.8624399141, -4.85334069691, -4.36313362838], [-66.5168886896, -119.565865367, -3.82156263224, -42.738513194, -17.997416403, -4.27306014466, 3.32437694401, 3.87380427124, 3.96650585072, -3.64331443007, -64.7699209174, -2.40288504726, -17.8675191027, -21.4090516507, -100.685737354, -94.9568417066, 2.75529179738, -6.76198346812, -248.183231263, 3.88918381776, -4.27710738849, -188.01675324, -35.8710345097, 2.53137740587, -3.56599891641, 2.5513737525, -38.3833456253, 3.36667907003, -4.184086347, -3.64590859672], [-68.9258691399, -124.85208597, -4.47718622911, -44.8850302678, -18.1720112785, -5.11864867685, 4.13240721268, 4.68849971056, 5.09427187906, -3.10715653914, -67.4769376665, -2.09403035631, -18.7274175634, -22.444490383, -105.020425326, -98.9506888686, 3.58938878545, -7.4752730703, -247.775010009, 4.95878983557, -5.4900469295, -196.259807802, -37.1653834231, 3.2455253427, -4.64972403122, 3.4832056153, -40.4769856435, 4.29214918113, -5.03693801977, -4.82461209935], [-58.282938783, -104.676742907, -3.27728106299, -37.3249821259, -15.6344334436, -3.95399653192, 2.86705167544, 3.58742863353, 3.38617878036, -2.77514410406, -56.81300061, -1.74695740442, -15.6547141121, -18.4446386004, -88.4482409853, -83.4000171263, 2.70730361767, -6.06502189472, -247.992864403, 3.17428854984, -4.01818153828, -164.945483526, -31.4187636911, 2.43915809895, -3.18087516175, 2.45922124585, -33.8082666555, 3.02029433208, -3.70864902966, -3.40166956561], [-57.3892367457, -102.596536537, -3.02230400751, -36.700052092, -15.5617189112, -3.50200233819, 2.60806803444, 2.99411150635, 3.30255603447, -3.90393656682, -55.7867784142, -3.57822906679, -15.091029781, -18.1533108941, -86.9115436976, -81.8257346464, 2.32586940617, -5.90165262742, -2.24130354988, 3.18196356299, -3.53383801216, -161.790848735, -30.9434220866, 2.05771865029, -3.09835513988, 2.06237152822, -33.2647853347, 2.48555723882, -3.50579056532, -3.09982532014], [-42.5817774363, -77.8036537245, -3.89435122326, -28.3961310781, -10.8882748353, -4.02878206967, 3.53891595539, 4.04193347091, 4.22246395229, -1.82618063779, -42.1317977378, -1.73570262956, -12.3767180637, -14.3588186125, -65.0338142004, -61.2029257845, 3.2245652203, -5.38255632509, 0.100297281461, 3.95881304948, -4.71494843598, -122.203361648, -22.6062084111, 3.13923688083, -3.67390321691, 3.01713505323, -25.4148465218, 3.62407320782, -4.51835011432, -3.92494032996], [-70.4351222971, -126.223376444, -3.43832493656, -45.0331499793, -19.1871526936, -4.14201912134, 3.10924650427, 3.56034830622, 3.78224044361, -3.9407639634, -68.1492997113, -2.69363948241, -18.3784049729, -22.354155603, -106.691876644, -100.713744919, 2.77073503631, -7.1245589713, -248.46758196, 3.30367601342, -4.14933517051, -198.686998702, -38.0979944564, 2.23744287824, -3.56984324078, 2.32347214721, -40.544239356, 2.9701367253, -3.87215438901, -3.39345284146], [-81.0115138152, -145.419087114, -4.28457245392, -52.0337703156, -21.8059244419, -5.07132689061, 3.88755776202, 4.64713612743, 4.63440622005, -5.20933560558, -78.9601338151, -5.10718628971, -21.5411093589, -25.9882861433, -122.951501539, -115.864305203, 3.40513172189, -8.49170792423, -1.96247564122, 4.51047905749, -5.03456542359, -229.008889338, -43.7999670021, 2.85086967639, -4.25061336884, 3.1733007141, -46.868675641, 3.88560827981, -4.67426034585, -4.18241211615], [-42.938952751, -78.62124076, -3.89467086954, -28.5337132753, -11.3621889921, -3.6723967395, 3.25338946405, 4.04892511289, 4.15309987846, -1.3222257949, -42.6986762591, -0.300333837194, -12.3845030156, -14.313036687, -65.5992862956, -62.1239593149, 3.00134077569, -5.3904776975, -246.998469427, 4.01545585467, -4.33074406496, -123.2809289, -22.8262074801, 2.67950675615, -3.52648610462, 3.02260923345, -25.475312528, 3.3485469311, -4.0210671888, -3.60466808733], [-12.9810306942, -25.7387219855, -3.78986396803, -9.9235624403, -2.44029800619, -3.02705329482, 3.7703260133, 4.07897732955, 4.14449716594, 1.36671307191, -14.2252480979, 2.49721607028, -5.16014416717, -5.64093534621, -20.5291175165, -19.0496404628, 3.51994808372, -3.10692430098, -239.16949629, 3.92232824332, -4.86120942, -39.639239719, -6.57129261706, 3.03057916945, -3.21469741836, 3.38744826268, -9.10205113516, 3.50073915022, -4.40226716348, -3.83500741206], [-29.1053855142, -54.5385249306, -4.9993712714, -20.2074338847, -6.49413941661, -4.61590274932, 4.79545756332, 5.31329572922, 5.58372511354, 0.860796809327, -30.0417505309, 1.91266446809, -9.60610370673, -10.9430722572, -44.8184121548, -41.9359901459, 4.40758635176, -5.30000675155, -245.185419247, 5.27315227455, -6.19357105448, -85.2209018528, -15.0381200177, 3.91773217205, -4.27219449252, 4.19561319753, -18.3849234086, 4.61815913392, -5.77926565393, -5.35807306985], [-33.0200260785, -59.7483947313, -2.40517467139, -21.7860111936, -8.72942239474, -2.87821643604, 2.19360641592, 2.52841852425, 2.83778646124, -1.12423712665, -32.6039551736, 0.172544605636, -9.34393280594, -11.0431711212, -50.1985563641, -47.4145286137, 2.16898959091, -3.90688740548, -246.851003671, 2.62974189236, -3.18011743654, -94.2526214163, -17.4385952267, 2.01757180393, -2.27163253746, 1.84274706212, -19.3519547559, 2.49485661597, -3.01702667818, -2.71664387374], [-32.0211043438, -58.8382753154, -3.35285320876, -21.4731345207, -7.93585427747, -3.2928261807, 3.26221868046, 3.5722861517, 3.65518862623, -0.308065111305, -32.099045254, 0.622297744224, -9.38513205296, -11.0782173635, -49.0957446527, -46.1175927094, 2.72479439378, -4.16355300875, -246.518606942, 3.42696781619, -4.08335528144, -91.949410078, -16.7411332966, 2.38151082568, -2.86774670221, 2.49157502714, -19.5206085947, 2.8655671202, -3.59042069635, -3.20323326013], [-7.1590920583, -14.4497174049, -2.82130172474, -5.84407151345, -1.09297891583, -2.34109167207, 2.67078170895, 2.98085794131, 3.20529269492, 0.615038530054, -8.11683649616, 0.870143709799, -3.43625020144, -3.50466770977, -11.4461458968, -10.7257900314, 2.48498550247, -2.43758668675, -1.26750698169, 3.28837309143, -3.73306498776, -22.2398455034, -3.47540917811, 2.2083703952, -2.3534915619, 2.63493007762, -5.23061818476, 2.68912528733, -3.55759489899, -2.85307919111]], [[-40.7478440968], [59.9569941123], [74.5338943794], [59.7642504505], [-31.1136773989], [80.7052731533], [10.7098643562], [-1.29833203855], [8.16522833254], [-24.5362331402], [51.4967118627], [-19.9616747612], [80.6610753197], [80.0066755324], [-45.8119600217], [-33.7161597675], [20.6139442277], [74.579803676], [18.2687954261], [10.2918866929], [68.713400984], [20.7198048914], [-58.2547704041], [6.93557278683], [74.7412458466], [14.044676271], [67.5229093286], [14.7451746694], [76.839259434], [76.0374429573]]];
        const bias = [[-1.31866571789, 3.08678541069, 0.326356875492, -4.188757186, -0.0438252549844, 0.26928380096, -1.42569480237, -2.48683216771, 0.244955218206, -0.21535959232, -1.14528530896, 0.786739834671, -0.200987006274, -5.52776303595, -2.16879965335, -3.01670263335, -3.0262742147, 0.976644442457, -1.36181058919, -2.14571275913, -1.8369631734, -0.4023961619, -3.73547609696, 0.562813550777, -0.296423612577, -2.43546991708, -0.603789510444, -0.273149558003, -2.32542652901, -0.0702326614101], [4.29030766997, -8.04293149837, 9.14475403152, 13.8685493864, -5.85724430955, 7.52652778206, 15.035623871, 6.47551648866, 20.6204916094, 29.1163432263, -3.37739309216, -13.0937008762, 22.6667541717, -6.24847329638, 11.8114844382, 0.766910647395, 5.39453090093, 6.22094483943, 8.88148352392, -2.68689386199], [-120.642725115, -217.51195789, -7.63336661814, -77.9284829029, -31.9759045907, -8.79145416615, 6.98266408602, 8.02148909605, 8.45062940437, -6.3950868889, -117.971893706, -5.17449265588, -32.8291509348, -38.9608440917, -183.368921337, -172.972697403, 6.23225687994, -13.0743024424, -248.886067003, 7.94008684984, -8.95647742849, -342.661188015, -64.9090771498, 5.48988361223, -7.67649602104, 5.7685192632, -70.4749629624, 6.85996800696, -8.81766131884, -7.96377480988], [78.5289488908]];

        // Prediction:
        var reg = new MLPRegressor('logistic', layers, weights, bias);
        var prediction = reg.predict(features);
        console.log(prediction);

    }
}
"""
