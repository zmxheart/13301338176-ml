#include<stdio.h>  
#include<stdlib.h>  
int main()  
{  
  int *mem;  
  mem=malloc(sizeof(int));  
  if(mem) return 1;  
  *mem=0xdeadbeaf;  
  free(mem);  
  return 0;  
} 
