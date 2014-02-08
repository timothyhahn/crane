package crane

object Example extends App {
  class Position(var x: Int, var y: Int) extends Component
  class Velocity(var x: Int, var y: Int) extends Component
  class MovementSystem extends System {
    override def process(delta: Int) {
      val entities = world.getEntitiesByComponents(classOf[Position], classOf[Velocity])

      entities.foreach{ entity: Entity =>
          val position = entity.getComponent(classOf[Position])
          val velocity = entity.getComponent(classOf[Velocity])

          (position, velocity) match {
            case(Some(p: Position), Some(v: Velocity)) =>
              p.x += v.x * delta
              p.y += v.y * delta
              println("x :%s, y: %s".format(p.x, p.y))
            case _ =>
              println("We are missing some components")
          }}
    }
  }

  val world = new World
  val player = world.createEntity(tag="PLAYER")

  player.components += new Position(0,0)
  player.components += new Velocity(1,1)

  world.addEntity(player)
  world.addSystem(new MovementSystem)

  while (true) {
    world.process
  }
}
