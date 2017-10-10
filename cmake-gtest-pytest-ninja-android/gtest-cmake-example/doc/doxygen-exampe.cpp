/*! A test class */
class Afterdoc_Test
{
  public:
    /** An enum type. 
     *  The documentation block cannot be put after the enum! 
     */
    enum EnumType
    {
      int EVal1,     /**< enum value 1 */
      int EVal2      /**< enum value 2 */
    };
    void member();   //!< a member function.
    
  protected:
    int value;       /*!< an integer value */
};

//!  A test class. 
/*!
  A more elaborate class description.
*/
class QTstyle_Test
{
  public:
    //! An enum.
    /*! More detailed enum description. */
    enum TEnum { 
                 TVal1, /*!< Enum value TVal1. */  
                 TVal2, /*!< Enum value TVal2. */  
                 TVal3  /*!< Enum value TVal3. */  
               } 
         //! Enum pointer.
         /*! Details. */
         *enumPtr, 
         //! Enum variable.
         /*! Details. */
         enumVar;  
    
    //! A constructor.
    /*!
      A more elaborate description of the constructor.
    */
    QTstyle_Test();
    //! A destructor.
    /*!
      A more elaborate description of the destructor.
    */
   ~QTstyle_Test();
    
    //! A normal member taking two arguments and returning an integer value.
    /*!
      \param a an integer argument.
      \param s a constant character pointer.
      \return The test results
      \sa QTstyle_Test(), ~QTstyle_Test(), testMeToo() and publicVar()
    */
    int testMe(int a,const char *s);
       
    //! A pure virtual member.
    /*!
      \sa testMe()
      \param c1 the first argument.
      \param c2 the second argument.
    */
    virtual void testMeToo(char c1,char c2) = 0;
   
    //! A public variable.
    /*!
      Details.
    */
    int publicVar;
       
    //! A function variable.
    /*!
      Details.
    */
    int (*handler)(int a,int b);
};




/**
 *  A test class. A more elaborate class description.
 */
class Javadoc_Test
{
  public:
    /** 
     * An enum.
     * More detailed enum description.
     */
    enum TEnum { 
          TVal1, /**< enum value TVal1. */  
          TVal2, /**< enum value TVal2. */  
          TVal3  /**< enum value TVal3. */  
         } 
       *enumPtr, /**< enum pointer. Details. */
       enumVar;  /**< enum variable. Details. */
       
      /**
       * A constructor.
       * A more elaborate description of the constructor.
       */
      Javadoc_Test();
      /**
       * A destructor.
       * A more elaborate description of the destructor.
       */
     ~Javadoc_Test();
    
      /**
       * a normal member taking two arguments and returning an integer value.
       * @param a an integer argument.
       * @param s a constant character pointer.
       * @see Javadoc_Test()
       * @see ~Javadoc_Test()
       * @see testMeToo()
       * @see publicVar()
       * @return The test results
       */
       int testMe(int a,const char *s);
       
      /**
       * A pure virtual member.
       * @see testMe()
       * @param c1 the first argument.
       * @param c2 the second argument.
       */
       virtual void testMeToo(char c1,char c2) = 0;
   
      /** 
       * a public variable.
       * Details.
       */
       int publicVar;
       
      /**
       * a function variable.
       * Details.
       */
       int (*handler)(int a,int b);
};








/*! \file structcmd.h
    \brief A Documented file.
    
    Details.
*/
/*! \def MAX(a,b)
    \brief A macro that returns the maximum of \a a and \a b.
   
    Details.
*/
/*! \var typedef unsigned int UINT32
    \brief A type definition for a .
    
    Details.
*/
/*! \var int errno
    \brief Contains the last error code.
    \warning Not thread safe!
*/
/*! \fn int open(const char *pathname,int flags)
    \brief Opens a file descriptor.
    \param pathname The name of the descriptor.
    \param flags Opening flags.
*/
/*! \fn int close(int fd)
    \brief Closes the file descriptor \a fd.
    \param fd The descriptor to close.
*/
/*! \fn size_t write(int fd,const char *buf, size_t count)
    \brief Writes \a count bytes from \a buf to the filedescriptor \a fd.
    \param fd The descriptor to write to.
    \param buf The data buffer to write.
    \param count The number of bytes to write.
*/
/*! \fn int read(int fd,char *buf,size_t count)
    \brief Read bytes from a file descriptor.
    \param fd The descriptor to read from.
    \param buf The buffer to read into.
    \param count The number of bytes to read.
*/
#define MAX(a,b) (((a)>(b))?(a):(b))
typedef unsigned int UINT32;
int errno;
int open(const char *,int);
int close(int);
size_t write(int,const char *, size_t);
int read(int,char *,size_t);


"""@package docstring
Documentation for this module.
More details.
"""
def func():
    """Documentation for a function.
    More details.
    """
    pass
class PyClass:
    """Documentation for a class.
    More details.
    """
   
    def __init__(self):
        """The constructor."""
        self._memVar = 0;
   
    def PyMethod(self):
        """Documentation for a method."""
        pass
     


















## @package pyexample
#  Documentation for this module.
#
#  More details.
## Documentation for a function.
#
#  More details.
def func():
    pass
## Documentation for a class.
#
#  More details.
class PyClass:
   
    ## The constructor.
    def __init__(self):
        self._memVar = 0;
   
    ## Documentation for a method.
    #  @param self The object pointer.
    def PyMethod(self):
        pass
     
    ## A class variable.
    classVar = 0;
    ## @var _memVar
    #  a member variable





