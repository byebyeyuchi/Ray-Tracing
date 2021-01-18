
LIU YUQI 260861483
 
Total: 15 marks

Objective 1-10 implemented as required: 11 marks
Objective 11: 4 marks

Objective 10:
Homelanders.xml and Homelanders.png
This is scene where I recreate multiple characters from assignmeent1


Objective 11:
1. Reflection(MirrorTwoSpheres.xml & MirrorTwoSpheres.png) - 0.5 mark
in Scene.getColor(). I set an additional parameter to Material class to give a reflection texture
Compute reflection color if result.material.reflect is not null

2. Quadric(Quadric.xml & Quadric.png) - 0.5 mark
In Quadric.java, according xml is Quadric.xml

3. Multi-threading Parallelization - 0.5 mark
The MiracleWorker class in Scene.java.
I split the window into 4 parts: top left, bottom left, top right, and bottom right
We run these 4 threads at the same tim

4. Depth of Blur(DofFourSphere.xml & DofFourSphere.png) - 1 mark
Implemented in MiracleWorker.run() in the the Scene.java.
I set an additional parameter to Scene class, if set depthBlur variable in xml and
give focalLength variable and blur samples number variable, we calculate the "blured ray"
and add it to a rays list

5. Area Light(bunny.xml & Bunny.png) - 1 mark
Implemented in Scene.getLight().
The algorithm is exactly the same as in the textbook FCG
Note: All scenes contain soft shadow by area light. bunny.xml is the one which best
shows the shadow. However, the rendering time on my laptop( 8 RAM MacOS) is around 50 minutes.

6. MetaBalls (xml files were given) - 0.5 mark
Implemented in MetaBall.java

