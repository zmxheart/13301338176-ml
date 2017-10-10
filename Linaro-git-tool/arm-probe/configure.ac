#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.66])
AC_INIT(libarmep, 0.1, andy.green@linaro.org)
AC_CONFIG_SRCDIR([arm-probe/arm-probe.c])
AC_CONFIG_HEADERS([config.h])

AM_INIT_AUTOMAKE([-Wall -Werror foreign])
AM_PROG_AR
LT_INIT(shared)


#AX_PTHREAD

# Checks for programs.
AM_PROG_CC_C_O
#AC_PROG_CC
AC_PROG_INSTALL
AC_PROG_MAKE_SET
AC_CONFIG_MACRO_DIR([m4])

# Checks for header files.
AC_CHECK_HEADERS([zlib.h fcntl.h netinet/in.h stdlib.h string.h sys/socket.h unistd.h sys/prctl.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_TYPE_SIZE_T

# Checks for library functions.

AC_FUNC_MALLOC
AC_FUNC_REALLOC
AC_CHECK_FUNCS([poll memset ])

AC_CONFIG_FILES([Makefile
                 libarmep/Makefile
                 arm-probe/Makefile
		 aepd/Makefile])

AC_OUTPUT

