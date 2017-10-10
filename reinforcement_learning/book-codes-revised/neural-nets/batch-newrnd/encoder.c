int encoder(double [],double [],double [],double [],double [],double []);
int encoder(double technicians[],double engineers[],double
computers[],double logicals[],double physicals[],double target[])
{
int count;
for(count=0;count<SIZE;count++)
{

technicians[count]=(technicians[count]-MINT)/(MAXT-MINT);
engineers[count]=(engineers[count]-MINE)/(MAXE-MINE);
computers[count]=(computers[count]-MINC)/(MAXC-MINC);
logicals[count]=(logicals[count]-MINL)/(MAXL-MINP);
physicals[count]=(physicals[count]-MINP)/(MAXP-MINP);
target[count]=(target[count]-MIN_TARGET)/(MAX_TARGET-MIN_TARGET);
}
return(1);
}
