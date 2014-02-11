package crane.examples

/** Crane Imports **/
import crane.{Entity, Component, System, EntityProcessingSystem, TimedSystem, IntervalSystem, World}

/** External Imports **/
import com.github.nscala_time.time.Imports._

/**
 * This example shows how to create Components, Systems, and use them.
 */
object Example extends App {
  
  /** Components **/
  class Position(var x: Int, var y: Int) extends Component {
    override def toString: String = {
      "\t Position x:%s, y:%s".format(x, y)
    }
  }
  class Velocity(var x: Int, var y: Int) extends Component {
    override def toString: String = {
      "\t Velocity x:%s, y:%s".format(x, y)
    }
  }
  class Useless extends Component {
    override def toString: String = {
      "\t Useless Please don't banana bread me"
    }
  }

  /** Systems **/
  class MovementSystem extends System {
    override def process(delta: Int) {
      val entities = world.getEntitiesByComponents(classOf[Position], classOf[Velocity])

      // If you need exclusions
      // val entities = world.getEntitiesWithExclusions(include=List(classOf[Position], classOf[Velocity]), exclude=List(classOf[Useless]))

      entities.foreach{ entity: Entity =>
          val position = entity.getComponent(classOf[Position])
          val velocity = entity.getComponent(classOf[Velocity])

          (position, velocity) match {
            case(Some(p: Position), Some(v: Velocity)) =>
              p.x += v.x * delta
              p.y += v.y * delta
              println(entity)
            case _ =>
              println("We are missing some components")
          }}
    }
  }

  // Equivalent to the system above, except you specify each individual entity
  class MovementEntityProcessingSystem extends EntityProcessingSystem(include=List(classOf[Position], classOf[Velocity])) {
    override def processEntity(e: Entity, delta: Int) {
      val position = e.getComponent(classOf[Position])
      val velocity = e.getComponent(classOf[Velocity])

      (position, velocity) match {
        case(Some(p: Position), Some(v: Velocity)) =>
          p.x += v.x * delta
          p.y += v.y * delta
          println(e)
        case _ =>
          println("We are missing some components")
      }}
  }

  // Example of a system that processes every n milliseconds
  class CustomTimedSystem(milliseconds: Int) extends System {
    var start = DateTime.now
    override def process(delta: Int) {
      if((start to DateTime.now).millis >= milliseconds) {
        val entities = world.getEntitiesByComponents(classOf[Useless])
        println("Killing %d entities".format(entities.length))
        entities.foreach { entity: Entity =>
          entity.kill
          println("Banana bread") 
        }
        start = DateTime.now
      }
    }
  }

  // Equivalent to above 
  class MyTimedSystem extends TimedSystem(3000) {
    override def processTime (delta: Int) {
      val entities = world.getEntitiesByComponents(classOf[Useless])
        println("Killing %d entities".format(entities.length))
        entities.foreach { entity: Entity =>
          entity.kill
          println("Banana bread") 
        }
    }
  }

  // Another Special System that runs "processInterval" every nth time
  class MyIntervalSystem extends IntervalSystem(100) {
    override def processInterval(delta: Int) {
        val entities = world.getEntitiesByComponents(classOf[Useless])
        println("Killing %d entities".format(entities.length))
        entities.foreach { entity: Entity =>
          entity.kill
          println("Banana bread") 
        }
    }
  }


  // Create World
  val world = World()
  // Default is already 1 - this does not need to be set unless you need variable deltas
  // world.delta = 1

  // Create entities
  val player = world.createEntity(tag="PLAYER")
  player.components += new Position(0,0)
  player.components += new Velocity(1,1)

  val useless = world.createEntity(tag="USELESS")
  useless.components += new Position(0,0)
  useless.components += new Velocity(2,0)
  useless.components += new Useless

  // Use this to make useless show up in the movement system
  //useless.removeComponent(classOf[Useless])

  world.addEntity(player)
  world.addEntity(useless)
  world.addSystem(new MovementSystem)
  world.addSystem(new CustomTimedSystem(3000), 1)

  world.createGroup("THINGS")
  world.groups("THINGS") += player
  world.groups("THINGS") += useless

  // Use this to remove entity
  // world.removeEntity(useless)

  while (true) { 
    world.process
  }
}
