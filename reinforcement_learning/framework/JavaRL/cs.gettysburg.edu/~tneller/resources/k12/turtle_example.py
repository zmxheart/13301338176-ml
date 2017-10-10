# Turtle programming example - Todd W. Neller
# http://cs.gettysburg.edu/~tneller/index.html

from turtle import *
setup(width=400, height=400, startx=200, starty=200)

# black triangle
forward(100)
left(120)
forward(100)
left(120)
forward(100)

up()
right(60)
forward(100)

# green circle
color("green")
down()
circle(50)

def square():
    forward(25)
    left(90)
    forward(25)
    left(90)
    forward(25)
    left(90)
    forward(25)
    left(90)

up()
right(90)
forward(100)

# blue square
color("blue")
down()
square()

# circle of blue squares
speed("fastest")
for i in range(60):
    forward(3)
    right(6)
    square()

