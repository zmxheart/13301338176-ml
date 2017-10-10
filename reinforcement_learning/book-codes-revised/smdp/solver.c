void solver(double G[NS][NS+1],double x[NS])
{
/* Using GAUSS JORDAN ELIMINATION */
int row, col,row1,col1,col2,pivot_row;
double factor,pivot,temp;

/* Find max element */

for(col=0;col<=NS-1;col++)
{
pivot=-0.1;
	/* Find the best pivot */
	for(row=col;row<=NS-1;row++)
	{
          if(fabs(G[row][col])>pivot)
          {
          pivot=fabs(G[row][col]);
          pivot_row=row;
          }
        }     
	/* To check if solution can be found */
	if(pivot<=0.00001)
        {/* pivot is 0 or else too small - will lead to errors */
        printf("Error in policy evaluation.  Singular matrix.\n");
	exit(0);
        }
       
        if(pivot_row!=col)
        {/* exchange rows to use the best pivot */
        	for(col1=col;col1<=NS;col1++)
		{
		temp=G[col][col1];
		G[col][col1]=G[pivot_row][col1];
		G[pivot_row][col1]=temp;
		}
	}

        /* Do Elimination */
        for(row1=0;row1<=NS-1;row1++)
	{
		if(row1!= col)
		{
		factor=G[row1][col]/G[col][col];
			for(col2=col;col2<=NS;col2++)
			{
			G[row1][col2] -=factor*G[col][col2];
			}
		}
	}
}

/* FInd solution */
for(row=0;row<NS;row++)
{
x[row]=G[row][NS]/G[row][row];
}


return;
}

