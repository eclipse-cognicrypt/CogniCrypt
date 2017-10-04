defaultScope(1);
intRange(-8, 7);
stringLength(16);

c0_Installation = Clafer("c0_Installation").withCard(1, 1);
c0_Status = c0_Installation.addChild("c0_Status").withCard(1, 1).withGroupCard(1, 1);
c0_OK = c0_Status.addChild("c0_OK").withCard(0, 1);
c0_Bad = c0_Status.addChild("c0_Bad").withCard(0, 1);
c0_Time = c0_Installation.addChild("c0_Time").withCard(1, 1);
c0_Time.refTo(Int);
