package io.bonitoo.virdev.plugin.rotat;

public class Rotation {

  // instantaneous state
  double roll; // rads - around longitudinal axis (cartwheeling - fall left or right) -180 (-Pi) to +180 (+Pi)
  double pitch; // rads - around transverse axis (tumbling - fall forward or back) -90 to + 90
  double yaw; // rads - around vertical axis (turning - pirouette - get dizzy )  -180 (-Pi) to +180 (+Pi)

  // Deltas
  // e.g. 2*PI = one full revolution about given access per second
  double rollDelta; // rads/sec
  double pitchDelta; // rads/sec

  double yawDelta; // rads/sec

}

/*

accelerationX = (signed int)(((signed int)rawData_X) * 3.9);
accelerationY = (signed int)(((signed int)rawData_Y) * 3.9);
accelerationZ = (signed int)(((signed int)rawData_Z) * 3.9);
pitch = 180 * atan (accelerationX/sqrt(accelerationY*accelerationY + accelerationZ*accelerationZ))/M_PI;
roll = 180 * atan (accelerationY/sqrt(accelerationX*accelerationX + accelerationZ*accelerationZ))/M_PI;
yaw = 180 * atan (accelerationZ/sqrt(accelerationX*accelerationX + accelerationZ*accelerationZ))/M_PI;
 *
 */
