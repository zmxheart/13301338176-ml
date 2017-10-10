int timer(double event_clock[])
{
  double minimum=BIG;
  int index,next_event;

  /* Find out which event is next */

  for(index=0;index<NUMBER_OF_EVENTS;index=index+1)
  {
    if(event_clock[index]<=minimum)
    {
      minimum=event_clock[index];

      next_event=index;

    }
  }

  /* advance simulation clock */

  TNOW=minimum;

/* return the next event */
return(next_event);
}
