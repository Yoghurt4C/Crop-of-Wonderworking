package mods.coww.api.power;

/**
 * Any blockentity that implements this is defined as having a direction,
 * and has two rotations (X and Y).
 */
public interface DirectionedInterface {

    /**
     * @return The X rotation (pitch), in degrees
     */
    public float getRotationX();

    /**
     * @return The Y rotation (yaw), in degrees
     */
    public float getRotationY();

    /**
     * Set the X rotation
     * @param rot X rotation, in degrees
     */
    public void setRotationX(float rot);

    /**
     * Set the Y rotation
     * @param rot Y rotation, in degrees
     */
    public void setRotationY(float rot);

    /**
     * This should be called after rotation setting is done to allow
     * for the block to re-calculate.
     */
    public void commitRedirection();

}