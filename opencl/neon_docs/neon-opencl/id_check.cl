__kernel void id_check(__global float *output) {
size_t
size_t
size_t
size_t
size_t
size_t
size_t
global_id_0 = get_global_id(0);
global_id_1 = get_global_id(1);
global_size_0 = get_global_size(0);
offset_0 = get_global_offset(0);
offset_1 = get_global_offset(1);
local_id_0 = get_local_id(0);
local_id_1 = get_local_id(1);
Access item/
group info
int index_0 = global_id_0 - offset_0;
int index_1 = global_id_1 - offset_1;
int index = index_1 * global_size_0 + index_0;
Determine
array index
float f = global_id_0 * 10.0f + global_id_1 * 1.0f;
f += local_id_0 * 0.1f + local_id_1 * 0.01f;
Set float
data
output[index] = f;
}
