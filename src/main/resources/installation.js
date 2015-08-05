defaultScope(1);
intRange(-8, 7);
stringLength(16);

c0_main = Clafer("c0_main").withCard(1, 1);
c0_age = c0_main.addChild("c0_age").withCard(1, 1);
c0_height = c0_main.addChild("c0_height").withCard(1, 1);
c0_student = Clafer("c0_student").withCard(1, 1);
c0_hi = c0_student.addChild("c0_hi").withCard(1, 1);
c0_roomnumber = c0_student.addChild("c0_roomnumber").withCard(1, 1);
c0_age.refTo(Int);
c0_height.refTo(Int);
c0_hi.refTo(c0_main);
c0_roomnumber.refTo(Int);
c0_roomnumber.addConstraint(equal(joinRef($this()), joinRef(join(joinRef(join(joinParent($this()), c0_hi)), c0_age))));
