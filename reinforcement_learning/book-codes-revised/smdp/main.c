# include <stdlib.h>
# include <stdio.h>
# include <math.h>
# include "global.h"
# include "fun_prots.h"
# include "fun_list.h"
main()
{

double tpm[NA][NS][NS]={
    {
	{0.7,0.3},
	{0.4,0.6}
    },
    {
        {0.9,0.1},
        {0.2,0.8}
    }
                        };

double trm[NA][NS][NS]={
       {
        {6,-5},
        {7,12}
       },
       {
        {10,17},
        {-14,13}
                 }
                        };    



double ttm[NA][NS][NS]={
       {
	{1,5},
	{120,60}
       },
       {
        {50,75},
        {7,2}
                 }
                        };



pias(tpm,trm,ttm);

return;
}
