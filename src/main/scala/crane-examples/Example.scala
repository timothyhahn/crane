import crane.{Entity, Component, System, World}

object Example extends App {
  class Position(var x: Int, var y: Int) extends Component
  class Velocity(var x: Int, var y: Int) extends Component
  class Useless extends Component

  class MovementSystem extends System {
    override def process(delta: Int) {
      val entities = world.getEntitiesWithExclusions(include=List(classOf[Position], classOf[Velocity]), exclude=List(classOf[Useless]))
      // If you don't need exclusions, use
      // val entities = world.getEntitiesByComponents(classOf[Position], classOf[Velocity])

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

  val world = new World
  val player = world.createEntity(tag="PLAYER")

  player.components += new Position(0,0)
  player.components += new Velocity(1,1)

  val useless = world.createEntity(tag="USELESS")
  useless.components += new Position(0,0)
  useless.components += new Velocity(2,0)
  useless.components += new Useless

  world.addEntity(player)
  world.addEntity(useless)
  world.addSystem(new MovementSystem)

  while (true) {
    world.process
  }
}
