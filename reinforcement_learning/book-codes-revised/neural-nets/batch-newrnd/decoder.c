int decoder(double []);
int decoder(double values[])
{
values[0]=(values[0]*(MAXT-MINT))+MINT;
values[1]=(values[1]*(MAXE-MINE))+MINE;
values[2]=(values[2]*(MAXC-MINC))+MINC;
values[3]=(values[3]*(MAXL-MINL))+MINL;
values[4]=(values[4]*(MAXP-MINP))+MINP;

return(1);
}
