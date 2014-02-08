package crane.examples
import crane.{Entity, Component, System, World}
import com.github.nscala_time.time.Imports._


object Example extends App {
  class Position(var x: Int, var y: Int) extends Component
  class Velocity(var x: Int, var y: Int) extends Component
  class Useless extends Component

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
              println("%s: x :%s, y: %s".format(entity.tag, p.x, p.y))
            case _ =>
              println("We are missing some components")
          }}
    }
  }

  class TimedSystem(milliseconds: Int) extends System {
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

  val world = new World
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
  world.addSystem(new TimedSystem(3000))

  world.createGroup("THINGS")
  world.groups("THINGS") += player
  world.groups("THINGS") += useless

  // Use this to remove entity
  // world.removeEntity(useless)

  while (true) { 
    world.process
  }
}
