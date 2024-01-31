/*
package snownee.fruits.vacuum.client;

import processing.core.*;
import processing.data.*;

import java.applet.*;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.Image;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;

public class verlet_simulator extends PApplet {


	// Where we'll store all of the points
	ArrayList<PointMass> pointmasses;

	// every PointMass within this many pixels will be influenced by the cursor
	float mouseInfluenceSize = 20;
	// minimum distance for tearing when user is right clicking
	float mouseTearSize = 8;
	float mouseInfluenceScalar = 5;

	// amount to accelerate everything downward
	float gravity = 980;

	// Dimensions for our curtain. These are number of PointMasss for each direction, not actual widths and heights
// the true width and height can be calculated by multiplying restingDistances by the curtain dimensions
	final int curtainHeight = 40;
	final int curtainWidth = 60;
	final int yStart = 25; // where will the curtain start on the y axis?
	final float restingDistances = 6;
	final float stiffnesses = 1;
	final float curtainTearSensitivity = 50; // distance the PointMasss have to go before ripping

	// Physics, see physics.pde
	Physics physics;

	public void setup() {
		size(640, 480);

		physics = new Physics();

		// we square the mouseInfluenceSize and mouseTearSize so we don't have to use squareRoot when comparing distances with this.
		mouseInfluenceSize *= mouseInfluenceSize;
		mouseTearSize *= mouseTearSize;

		// We use an ArrayList instead of an array so we can add or remove PointMasss at will.
		// not that it isn't possible using an array, it's just more convenient this way
		pointmasses = new ArrayList<PointMass>();

		// create the curtain
		createCurtain();

		// create the ragdolls
		createBodies();
	}

	public void draw() {
		background(255);

		physics.update();

		updateGraphics();

		// Print frame rate every now and then
//  if (frameCount % 60 == 0)
//    println("Frame rate is " + frameRate);
	}

	// Draw everything
	public void updateGraphics() {
		for (PointMass p : pointmasses) {
			p.draw();
		}
		for (Circle c : physics.circles) {
			c.draw();
		}
	}

	public void addPointMass(PointMass p) {
		pointmasses.add(p);
	}

	public void removePointMass(PointMass p) {
		pointmasses.remove(p);
	}


	public void createCurtain() {
		// midWidth: amount to translate the curtain along x-axis for it to be centered
		// (curtainWidth * restingDistances) = curtain's pixel width
		int midWidth = (int) (width / 2 - (curtainWidth * restingDistances) / 2);
		// Since this our fabric is basically a grid of points, we have two loops
		for (int y = 0; y <= curtainHeight; y++) { // due to the way PointMasss are attached, we need the y loop on the outside
			for (int x = 0; x <= curtainWidth; x++) {
				PointMass pointmass = new PointMass(midWidth + x * restingDistances, y * restingDistances + yStart);

				// attach to
				// x - 1  and
				// y - 1
				//  *<---*<---*<-..
				//  ^    ^    ^
				//  |    |    |
				//  *<---*<---*<-..
				//
				// PointMass attachTo parameters: PointMass PointMass, float restingDistance, float stiffness
				// try disabling the next 2 lines (the if statement and attachTo part) to create a hairy effect
				if (x != 0)
					pointmass.attachTo((PointMass) (pointmasses.get(pointmasses.size() - 1)), restingDistances, stiffnesses);
				// the index for the PointMasss are one dimensions,
				// so we convert x,y coordinates to 1 dimension using the formula y*width+x
				if (y != 0)
					pointmass.attachTo((PointMass) (pointmasses.get((y - 1) * (curtainWidth + 1) + x)), restingDistances, stiffnesses);

				// we pin the very top PointMasss to where they are
				if (y == 0)
					pointmass.pinTo(pointmass.x, pointmass.y);

				// add to PointMass array
				pointmasses.add(pointmass);
			}
		}
	}

	public void createBodies() {
		for (int i = 0; i < 25; i++) {
			new Body(random(width), random(height), 40);
		}
	}

	// Controls. The r key resets the curtain, g toggles gravity
	public void keyPressed() {
		if ((key == 'r') || (key == 'R')) {
			pointmasses = new ArrayList<PointMass>();
			physics.circles = new ArrayList<Circle>();
			createCurtain();
			createBodies();
		}
		if ((key == 'g') || (key == 'G'))
			toggleGravity();
	}

	public void toggleGravity() {
		if (gravity != 0)
			gravity = 0;
		else
			gravity = 980;
	}

	// Using http://www.codeguru.com/forum/showpost.php?p=1913101&postcount=16
// We use this to have consistent interaction
// so if the cursor is moving fast, it won't interact only in spots where the applet registers it at
	public float distPointToSegmentSquared(float lineX1, float lineY1, float lineX2, float lineY2, float pointX, float pointY) {
		float vx = lineX1 - pointX;
		float vy = lineY1 - pointY;
		float ux = lineX2 - lineX1;
		float uy = lineY2 - lineY1;

		float len = ux * ux + uy * uy;
		float det = (-vx * ux) + (-vy * uy);
		if ((det < 0) || (det > len)) {
			ux = lineX2 - pointX;
			uy = lineY2 - pointY;
			return min(vx * vx + vy * vy, ux * ux + uy * uy);
		}

		det = ux * vy - uy * vx;
		return (det * det) / len;
	}

	// Body
// Here we construct and store a ragdoll
	class Body {
		*/
/*
		   O
		  /|\
		 / | \
		  / \
		 |   |
		*//*

		PointMass head;
		PointMass shoulder;
		PointMass elbowLeft;
		PointMass elbowRight;
		PointMass handLeft;
		PointMass handRight;
		PointMass pelvis;
		PointMass kneeLeft;
		PointMass kneeRight;
		PointMass footLeft;
		PointMass footRight;
		Circle headCircle;

		float headWidth;
		float headLength;

		Body(float x, float y, float bodyHeight) {
			headLength = bodyHeight / 7.5f;
			headWidth = headLength * 3 / 4;

			head = new PointMass(x + random(-5, 5), y + random(-5, 5));
			head.mass = 4;
			shoulder = new PointMass(x + random(-5, 5), y + random(-5, 5));
			shoulder.mass = 26; // shoulder to torso
			head.attachTo(shoulder, 5 / 4 * headLength, 1, bodyHeight * 2, true);

			elbowLeft = new PointMass(x + random(-5, 5), y + random(-5, 5));
			elbowRight = new PointMass(x + random(-5, 5), y + random(-5, 5));
			elbowLeft.mass = 2; // upper arm mass
			elbowRight.mass = 2;
			elbowLeft.attachTo(shoulder, headLength * 3 / 2, 1, bodyHeight * 2, true);
			elbowRight.attachTo(shoulder, headLength * 3 / 2, 1, bodyHeight * 2, true);

			handLeft = new PointMass(x + random(-5, 5), y + random(-5, 5));
			handRight = new PointMass(x + random(-5, 5), y + random(-5, 5));
			handLeft.mass = 2;
			handRight.mass = 2;
			handLeft.attachTo(elbowLeft, headLength * 2, 1, bodyHeight * 2, true);
			handRight.attachTo(elbowRight, headLength * 2, 1, bodyHeight * 2, true);

			pelvis = new PointMass(x + random(-5, 5), y + random(-5, 5));
			pelvis.mass = 15; // pelvis to lower torso
			pelvis.attachTo(shoulder, headLength * 3.5f, 0.8f, bodyHeight * 2, true);
			// this restraint keeps the head from tilting in extremely uncomfortable positions
			pelvis.attachTo(head, headLength * 4.75f, 0.02f, bodyHeight * 2, false);

			kneeLeft = new PointMass(x + random(-5, 5), y + random(-5, 5));
			kneeRight = new PointMass(x + random(-5, 5), y + random(-5, 5));
			kneeLeft.mass = 10;
			kneeRight.mass = 10;
			kneeLeft.attachTo(pelvis, headLength * 2, 1, bodyHeight * 2, true);
			kneeRight.attachTo(pelvis, headLength * 2, 1, bodyHeight * 2, true);

			footLeft = new PointMass(x + random(-5, 5), y + random(-5, 5));
			footRight = new PointMass(x + random(-5, 5), y + random(-5, 5));
			footLeft.mass = 5; // calf + foot
			footRight.mass = 5;
			footLeft.attachTo(kneeLeft, headLength * 2, 1, bodyHeight * 2, true);
			footRight.attachTo(kneeRight, headLength * 2, 1, bodyHeight * 2, true);

			// these constraints resist flexing the legs too far up towards the body
			footLeft.attachTo(shoulder, headLength * 7.5f, 0.001f, bodyHeight * 2, false);
			footRight.attachTo(shoulder, headLength * 7.5f, 0.001f, bodyHeight * 2, false);

			headCircle = new Circle(headLength * 0.75f);
			headCircle.attachToPointMass(head);

			physics.addCircle(headCircle);
			addPointMass(head);
			addPointMass(shoulder);
			addPointMass(pelvis);
			addPointMass(elbowLeft);
			addPointMass(elbowRight);
			addPointMass(handLeft);
			addPointMass(handRight);
			addPointMass(kneeLeft);
			addPointMass(kneeRight);
			addPointMass(footLeft);
			addPointMass(footRight);
		}

		public void removeFromWorld() {
			physics.removeCircle(headCircle);
			removePointMass(head);
			removePointMass(shoulder);
			removePointMass(pelvis);
			removePointMass(elbowLeft);
			removePointMass(elbowRight);
			removePointMass(handLeft);
			removePointMass(handRight);
			removePointMass(kneeLeft);
			removePointMass(kneeRight);
			removePointMass(footLeft);
			removePointMass(footRight);
		}
	}

	// Circle
// used as a head for ragdolls
	class Circle {

		float radius;

		PointMass attachedPointMass;

		Circle(float r) {
			radius = r;
		}

		// Constraints
		public void solveConstraints() {
			float x = attachedPointMass.x;
			float y = attachedPointMass.y;

			// only do a boundary constraint
			if (y < radius)
				y = 2 * (radius) - y;
			if (y > height - radius)
				y = 2 * (height - radius) - y;
			if (x > width - radius)
				x = 2 * (width - radius) - x;
			if (x < radius)
				x = 2 * radius - x;

			attachedPointMass.x = x;
			attachedPointMass.y = y;
		}

		public void draw() {
			ellipse(attachedPointMass.x, attachedPointMass.y, radius * 2, radius * 2);
		}

		public void attachToPointMass(PointMass p) {
			attachedPointMass = p;
		}
	}

	// The Link class is used for handling distance constraints between PointMasss.
	class Link {
		float restingDistance;
		float stiffness;
		float tearSensitivity;

		PointMass p1;
		PointMass p2;

		// if you want this link to be invisible, set this to false
		boolean drawThis = true;

		Link(PointMass which1, PointMass which2, float restingDist, float stiff, float tearSensitivity, boolean drawMe) {
			p1 = which1; // when you set one object to another, it's pretty much a reference.
			p2 = which2; // Anything that'll happen to p1 or p2 in here will happen to the paticles in our ArrayList

			restingDistance = restingDist;
			stiffness = stiff;
			drawThis = drawMe;

			this.tearSensitivity = tearSensitivity;
		}

		// Solve the link constraint
		public void solve() {
			// calculate the distance between the two PointMasss
			float diffX = p1.x - p2.x;
			float diffY = p1.y - p2.y;
			float d = sqrt(diffX * diffX + diffY * diffY);

			// find the difference, or the ratio of how far along the restingDistance the actual distance is.
			float difference = (restingDistance - d) / d;

			// if the distance is more than curtainTearSensitivity, the cloth tears
			if (d > tearSensitivity)
				p1.removeLink(this);

			// Inverse the mass quantities
			float im1 = 1 / p1.mass;
			float im2 = 1 / p2.mass;
			float scalarP1 = (im1 / (im1 + im2)) * stiffness;
			float scalarP2 = stiffness - scalarP1;

			// Push/pull based on mass
			// heavier objects will be pushed/pulled less than attached light objects
			p1.x += diffX * scalarP1 * difference;
			p1.y += diffY * scalarP1 * difference;

			p2.x -= diffX * scalarP2 * difference;
			p2.y -= diffY * scalarP2 * difference;
		}

		// Draw if it's visible
		public void draw() {
			if (drawThis)
				line(p1.x, p1.y, p2.x, p2.y);
		}
	}

	// Physics
// Timesteps are managed here
	class Physics {
		// list of circle constraints
		ArrayList<Circle> circles = new ArrayList<Circle>();

		long previousTime;
		long currentTime;

		int fixedDeltaTime;
		float fixedDeltaTimeSeconds;

		int leftOverDeltaTime;

		int constraintAccuracy;

		Physics() {
			fixedDeltaTime = 16;
			fixedDeltaTimeSeconds = (float) fixedDeltaTime / 1000.0f;
			leftOverDeltaTime = 0;
			constraintAccuracy = 3;
		}

		// Update physics
		public void update() {
			// calculate elapsed time
			currentTime = millis();
			long deltaTimeMS = currentTime - previousTime;

			previousTime = currentTime; // reset previous time

			// break up the elapsed time into manageable chunks
			int timeStepAmt = (int) ((float) (deltaTimeMS + leftOverDeltaTime) / (float) fixedDeltaTime);

			// limit the timeStepAmt to prevent potential freezing
			timeStepAmt = min(timeStepAmt, 5);

			// store however much time is leftover for the next frame
			leftOverDeltaTime = (int) deltaTimeMS - (timeStepAmt * fixedDeltaTime);

			// How much to push PointMasses when the user is interacting
			mouseInfluenceScalar = 1.0f / timeStepAmt;

			// update physics
			for (int iteration = 1; iteration <= timeStepAmt; iteration++) {
				// solve the constraints multiple times
				// the more it's solved, the more accurate.
				for (int x = 0; x < constraintAccuracy; x++) {
					for (int i = 0; i < pointmasses.size(); i++) {
						PointMass pointmass = (PointMass) pointmasses.get(i);
						pointmass.solveConstraints();
					}
					for (int i = 0; i < circles.size(); i++) {
						Circle c = (Circle) circles.get(i);
						c.solveConstraints();
					}
				}

				// update each PointMass's position
				for (int i = 0; i < pointmasses.size(); i++) {
					PointMass pointmass = (PointMass) pointmasses.get(i);
					pointmass.updateInteractions();
					pointmass.updatePhysics(fixedDeltaTimeSeconds);
				}
			}
		}

		public void addCircle(Circle c) {
			circles.add(c);
		}

		public void removeCircle(Circle c) {
			circles.remove(c);
		}
	}

	// PointMass
	class PointMass {
		float lastX, lastY; // for calculating position change (velocity)
		float x, y;
		float accX, accY;

		float mass = 1;
		float damping = 20;

		// An ArrayList for links, so we can have as many links as we want to this PointMass
		ArrayList links = new ArrayList();

		boolean pinned = false;
		float pinX, pinY;

		// PointMass constructor
		PointMass(float xPos, float yPos) {
			x = xPos;
			y = yPos;

			lastX = x;
			lastY = y;

			accX = 0;
			accY = 0;
		}

		// The update function is used to update the physics of the PointMass.
		// motion is applied, and links are drawn here
		public void updatePhysics(float timeStep) { // timeStep should be in elapsed seconds (deltaTime)
			this.applyForce(0, mass * gravity);

			float velX = x - lastX;
			float velY = y - lastY;

			// dampen velocity
			velX *= 0.99f;
			velY *= 0.99f;

			float timeStepSq = timeStep * timeStep;

			// calculate the next position using Verlet Integration
			float nextX = x + velX + 0.5f * accX * timeStepSq;
			float nextY = y + velY + 0.5f * accY * timeStepSq;

			// reset variables
			lastX = x;
			lastY = y;

			x = nextX;
			y = nextY;

			accX = 0;
			accY = 0;
		}

		public void updateInteractions() {
			// this is where our interaction comes in.
			if (mousePressed) {
				float distanceSquared = distPointToSegmentSquared(pmouseX, pmouseY, mouseX, mouseY, x, y);
				if (mouseButton == LEFT) {
					if (distanceSquared < mouseInfluenceSize) { // remember mouseInfluenceSize was squared in setup()
						// To change the velocity of our PointMass, we subtract that change from the lastPosition.
						// When the physics gets integrated (see updatePhysics()), the change is calculated
						// Here, the velocity is set equal to the cursor's velocity
						lastX = x - (mouseX - pmouseX) * mouseInfluenceScalar;
						lastY = y - (mouseY - pmouseY) * mouseInfluenceScalar;
					}
				} else { // if the right mouse button is clicking, we tear the cloth by removing links
					if (distanceSquared < mouseTearSize)
						links.clear();
				}
			}
		}

		public void draw() {
			// draw the links and points
			stroke(0);
			if (links.size() > 0) {
				for (int i = 0; i < links.size(); i++) {
					Link currentLink = (Link) links.get(i);
					currentLink.draw();
				}
			} else
				point(x, y);
		}

		*/
/* Constraints *//*

		public void solveConstraints() {
			*/
/* Link Constraints *//*

			// Links make sure PointMasss connected to this one is at a set distance away
			for (int i = 0; i < links.size(); i++) {
				Link currentLink = (Link) links.get(i);
				currentLink.solve();
			}

			*/
/* Boundary Constraints *//*

			// These if statements keep the PointMasss within the screen
			if (y < 1)
				y = 2 * (1) - y;
			if (y > height - 1)
				y = 2 * (height - 1) - y;

			if (x > width - 1)
				x = 2 * (width - 1) - x;
			if (x < 1)
				x = 2 * (1) - x;

			*/
/* Other Constraints *//*

			// make sure the PointMass stays in its place if it's pinned
			if (pinned) {
				x = pinX;
				y = pinY;
			}
		}

		// attachTo can be used to create links between this PointMass and other PointMasss
		public void attachTo(PointMass P, float restingDist, float stiff) {
			attachTo(P, restingDist, stiff, 30, true);
		}

		public void attachTo(PointMass P, float restingDist, float stiff, boolean drawLink) {
			attachTo(P, restingDist, stiff, 30, drawLink);
		}

		public void attachTo(PointMass P, float restingDist, float stiff, float tearSensitivity) {
			attachTo(P, restingDist, stiff, tearSensitivity, true);
		}

		public void attachTo(PointMass P, float restingDist, float stiff, float tearSensitivity, boolean drawLink) {
			Link lnk = new Link(this, P, restingDist, stiff, tearSensitivity, drawLink);
			links.add(lnk);
		}

		public void removeLink(Link lnk) {
			links.remove(lnk);
		}

		public void applyForce(float fX, float fY) {
			// acceleration = (1/mass) * force
			// or
			// acceleration = force / mass
			accX += fX / mass;
			accY += fY / mass;
		}

		public void pinTo(float pX, float pY) {
			pinned = true;
			pinX = pX;
			pinY = pY;
		}
	}

	public int sketchWidth() {
		return 640;
	}

	public int sketchHeight() {
		return 480;
	}

	static public void main(String args[]) {
		PApplet.main(new String[]{"--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "verlet_simulator"});
	}
}
*/
