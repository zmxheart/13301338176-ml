double compute_sigmoid(double input)
{
/* this function returns the output of a sigmoid transfer function */

   if(input<-709.0)
   {
   return (1.0);
   }
   else if(input>709.0)
   {
   return (0.0);
   }
   else
   {
   return (1.0/(1.0+exp(-input)));
   }
}
