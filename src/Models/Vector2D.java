package Models;

public class Vector2D implements Cloneable {

    /**
     * Hello. JavaDoc...
     */

    public float x, y;

    /**
     * @return x value of Vector2D.
     */
    public float getX() { return x; }

    /**
     * @return y value of Vector2D.
     */
    public float getY() { return y; }

    public Vector2D getVector2D(){ return this; }

    /**
     * replace Vector2D's x value with a parameter.
     * @param x parameter for changing Vector2D's x value.
     */
    public void setX(float x) { this.x = x; }

    /**
     * replace Vector2D's y value with a parameter.
     * @param y parameter for changing Vector2D's y value.
     */
    public void setY(float y) { this.y = y; }


    public void setVector2D(float x, float y){
        setX(x);
        setY(y);
    }

    public void setVector2D(Vector2D vector2D){
        setVector2D(vector2D.x, vector2D.y);
    }

    /**
     * constructor with two parameters -x and y-.
     * @param x parameter for setting Vector2D's x value.
     * @param y parameter for setting Vector2D's y value.
     */
    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * constructor with a parameter -Vector2D-.
     * @param vector2D parameter for setting Vector2D.
     */
    public Vector2D(Vector2D vector2D) {
        this.x = vector2D.x;
        this.y = vector2D.y;
    }


    /**
     * get sign of a value.
     * @param value parameter for getting sign.
     * @return a sign of a value.
     */
    public int getSign(int value){
        if(value < 0) return -1;
        else if(value == 0) return 0;
        else return 1;
    }


    /**
     *
     * @param vector2D
     * @return
     */
    public static Vector2D getSigned(Vector2D vector2D){
        int x, y;
        x = vector2D.getSign((int)vector2D.x);
        y = vector2D.getSign((int)vector2D.y);
        return new Vector2D(vector2D.x * x , vector2D.y * y);
    }

    /**
     * change Vector2D with two parameters -x and y-. (Add)
     * @param x parameter for adding Vector2D's x value.
     * @param y parameter for adding Vector2D's y value.
     */
    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    /**
     * change Vector2D with a parameter -Vector2D-.
     * @param vector2D parameter for adding Vector2D.
     */
    public void add(Vector2D vector2D) { this.add(vector2D.x, vector2D.y); }

    /**
     * returns new Vector2D after add with three parameters -Vector2D, x and y-.
     * @param vector2D parameter for adding.
     * @param x        parameter for adding.
     * @param y        parameter for adding.
     * @return new Vector2D after add.
     */
    public static Vector2D getAdded(Vector2D vector2D, float x, float y) {
        Vector2D added = vector2D.clone();
        added.add(x, y);
        return added;
    }

    /**
     * returns new Vector2D after add with two parameters -two Vector2D objects-.
     * @param v1       parameter for adding.
     * @param v2       parameter for adding.
     * @return new Vector2D after add.
     */
    public static Vector2D getAdded(Vector2D v1, Vector2D v2) {
        Vector2D added = v1.clone();
        added.add(v2);
        return added;
    }

    /**
     * change Vector2D with two parameters -x and y-. (Subtract)
     * @param x        parameter for subtracting Vector2D's x value.
     * @param y        parameter for subtracting Vector2D's y value.
     */
    public void subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
    }
    /**
     * change Vector2D with a parameter -Vector2D-
     * @param vector2D parameter for subtracting Vector2D.
     */
    public void subtract(Vector2D vector2D) { this.subtract(vector2D.x, vector2D.y); }

    /**
     * returns new Vector2D after subtraction with three parameters -Vector2D, x and y-.
     * @param vector2D parameter for subtracting.
     * @param x        parameter for subtracting.
     * @param y        parameter for subtracting.
     * @return new Vector2D after subtraction.
     */
    public static Vector2D getSubtracted(Vector2D vector2D, float x, float y) {
        Vector2D subtracted = vector2D.clone();
        subtracted.subtract(x, y);
        return subtracted;
    }

    /**
     * returns new Vector2D after subtraction with two parameters -two Vector2D objects-.
     * @param v1 parameter for subtracting.
     * @param v2 parameter for subtracting.
     * @return new Vector2D after subtraction.
     */
    public static Vector2D getSubtracted(Vector2D v1, Vector2D v2) {
        Vector2D subtracted = v1.clone();
        v1.subtract(v2);
        return subtracted;
    }

    /**
     * change Vector2D with two parameters -x and y-. (Add)
     * @param x parameter for multiplying Vector2D's x value.
     * @param y parameter for multiplying Vector2D's y value.
     */
    public void multiply(float x, float y) {
        this.x *= x;
        this.y *= y;
    }

    /**
     * change Vector2D with a parameter -Vector2D-.
     * @param vector2D parameter for multiplying Vector2D.
     */
    public void multiply(Vector2D vector2D) { this.multiply(vector2D.x, vector2D.y); }

    /**
     * returns new Vector2D after multiplication with three parameters -Vector2D, x and y-.
     * @param vector2D parameter for multiplying.
     * @param x        parameter for multiplying.
     * @param y        parameter for multiplying.
     * @return new Vector2D after multiplication.
     */
    public static Vector2D getMultiplied(Vector2D vector2D, float x, float y) {
        Vector2D multiplied = vector2D.clone();
        multiplied.multiply(x, y);
        return multiplied;
    }

    /**
     * returns new Vector2D after multiplication with two parameters -two Vector2D objects-.
     * @param v1 parameter for multiplying.
     * @param v2 parameter for multiplying.
     * @return new Vector2D after multiplication.
     */
    public static Vector2D getMultiplied(Vector2D v1, Vector2D v2) {
        Vector2D multiplied = v1.clone();
        v1.multiply(v2);
        return multiplied;
    }

    /**
     * changes the x-value of Vector2D symmetrically. (standard: line X = x)
     * @param x parameter which will be used as a standard for changing x-value of Vector2D.
     */
    public void reflectX(float x) { this.x = 2 * x - this.x; }

    /**
     * changes the y-value of Vector2D symmetrically. (standard: line Y = y)
     * @param y parameter which will be used as a standard for changing y-value of Vector2D.
     */
    public void reflectY(float y) { this.y = 2 * y - this.y; }

    /**
     * changes two values -x and y- of Vector2D symmetrically. (standard: Point (x, y))
     * @param x parameter which will be used as a standard for changing x-value of Vector2D.
     * @param y parameter which will be used as a standard for changing y-value of Vector2D.
     */
    public void reflect(float x, float y) {
        reflectX(x);
        reflectY(y);
    }

    /**
     * changes two values -x and y- of Vector2D symmetrically. (standard: Vector2D)
     * @param vector2D parameter which will be used as a standard for changing Vector2D.
     */
    public void reflect(Vector2D vector2D) { reflect(vector2D.x, vector2D.y); }

    /**
     * returns new Vector2D after reflection with three parameters -Vector2D, x and y-.
     * @param vector2D parameter which will be reflected.
     * @param x        parameter which will be used as a standard.
     * @param y        parameter which will be used as a standard.
     * @return new Vector2D after reflection.
     */
    public static Vector2D getReflected(Vector2D vector2D, float x, float y) {
        Vector2D reflected = vector2D.clone();
        reflected.reflect(x, y);
        return reflected;
    }

    /**
     * returns new Vector2D after reflection with two parameters -two Vector2D objects-.
     * @param vector2D parameter which will be reflected.
     * @param base     parameter which will be used as a standard.
     * @return new Vector2D after reflection.
     */
    public static Vector2D getReflected(Vector2D vector2D, Vector2D base) {
        Vector2D reflected = vector2D.clone();
        reflected.reflect(base);
        return reflected;
    }

    /**
     * changes the sign of value x.
     */
    public void reverseX() { this.x *= -1; }

    /**
     * changes the sign of value y.
     */
    public void reverseY() { this.y *= -1; }

    /**
     * changes the sign of value x and y.
     */
    public void reverseAll() {
        reverseX();
        reverseY();
    }

    /**
     * returns the distance of two Vector2Ds with two parameters -two Vector2D objects-.
     * @param v1 parameter for getting distance.
     * @param v2 parameter for getting distance.
     * @return the distance of two Vector2Ds.
     */
    public static float getDistance(Vector2D v1, Vector2D v2) { return (float) Math.sqrt(Math.pow((v1.x - v2.x), 2) + Math.pow((v1.y - v2.y), 2)); }

    /**
     * returns the distance of two Vector2Ds with four parameters -two x values and two y values-.
     * @param x1 parameter for getting distance.
     * @param x2 parameter for getting distance.
     * @param y1 parameter for getting distance.
     * @param y2 parameter for getting distance.
     * @return the distance of two Vector2Ds.
     */
    public static float getDistance(float x1, float y1, float x2, float y2) { return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)); }


    public float distance(float x, float y) {
        return (float) Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
    }

    /**
     * returns the distance from Vector2D(0, 0) to itself.
     * @return the distance from Vector2D(0, 0) to itself.
     */
    public float magnitude() { return getDistance(this, new Vector2D(0, 0)); }

    /**
     * returns the distance from Vector2D(0, 0) to a vector2D.
     * @param vector2D parameter for getting distance.
     * @return the distance from Vector2D(0, 0) to a vector2D.
     */
    public static float magnitude(Vector2D vector2D) { return getDistance(vector2D, new Vector2D(0, 0)); }

    /**
     * returns the distance from Vector2D(0, 0) to a Vector2D(0, 0).
     * @param x parameter for getting distance.
     * @param y parameter for getting distance.
     * @return the distance from Vector2D(0, 0) to a Vector2D(0, 0).
     */
    public static float magnitude(float x, float y) { return getDistance(x, y, 0, 0); }

    /**
     * normalizes x value.
     */
    public void normalizeX() { this.x /= this.magnitude(); }

    /**
     * normalizes y value.
     */
    public void normalizeY() { this.y /= this.magnitude(); }

    /**
     * normalizes two values -x and y-.
     */
    public void normalize() {
        normalizeX();
        normalizeY();
    }

    /**
     * returns new Vector2D after normalization.
     * @param vector2D parameter for normalizing.
     * @return new Vector2D after normalization.
     */
    public static Vector2D getNormalized(Vector2D vector2D) {
        Vector2D normalized = vector2D.clone();
        normalized.normalize();
        return normalized;
    }

    /**
     * rotates Vector2D based on a Vector2D(base) to the extent of (angle).
     * @param angle parameter for rotating.
     * @param base  parameter for rotating.
     */
    public void rotate(float angle, Vector2D base) {

        Vector2D tmpVector = this.clone();
        tmpVector.subtract(base);
        tmpVector.x = (float) (Math.cos(angle) * x - Math.sin(angle) * y);
        tmpVector.y = (float) (Math.sin(angle) * x + Math.cos(angle) * y);
        tmpVector.add(base);
        setVector2D(tmpVector);
    }

    /**
     * rotates Vector2D based on a Vector2D(0, 0) to the extent of (angle).
     * @param angle parameter for rotating.
     */
    public void rotateFromOrigin(float angle) { rotate(angle, new Vector2D(0, 0)); }

    /**
     * returns new Vector2D after rotation with three parameters -Vector2D, angle, and Vector2D(base)-.
     * @param vector2D parameter which will be rotated.
     * @param angle    parameter used as the extent for rotating.
     * @param base     parameter which will be used as a standard.
     * @return new Vector2D after rotation.
     */
    public static Vector2D getRotated(Vector2D vector2D, float angle, Vector2D base) {
        Vector2D rotated = getSubtracted(vector2D, base).clone();
        rotated.rotate(angle, base);
        return rotated;
    }

    /**
     * returns new Vector2D after rotation with two parameters -Vector2D, angle- based on a Vector2D(0, 0).
     * @param vector2D parameter which will be rotated.
     * @param angle    parameter used as the extent for rotating.
     * @return new Vector2D after rotation.
     */
    public static Vector2D getRotatedFromOrigin(Vector2D vector2D, float angle) {
        Vector2D rotated = vector2D.clone();
        rotated.rotateFromOrigin(angle);
        return rotated;
    }
    /**
     * clones Vector2D
     * @return cloned Vector2D object.
     */
    @Override
    public Vector2D clone() {
        try { return (Vector2D) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }return null;
    }


    /**
     * prints Vector 2D state
     */
    public void print(){ System.out.print("X: " + x + "Y:" + y); }

    /**
     * prints Vector 2D state
     */
    public void println(){ System.out.println("X: " + x + "Y:" + y); }
}