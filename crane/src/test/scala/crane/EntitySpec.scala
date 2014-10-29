package crane

import org.scalatest.FlatSpec
import org.scalamock.scalatest.MockFactory

class EntitySpec extends FlatSpec with MockFactory {
  "an entity" should "have a tag" in {
    var entity = Entity()
    assert(entity.tag == "")
    entity = new Entity("tag")
    assert(entity.tag == "tag")
  }

  it should "have components" in {
    val entity = Entity()
    val component = mock[Component]
    entity.components += component
    assert(entity.components.length == 1)

    assert(entity.getComponent[Component] match {
      case Some(s) => true
      case _ => false
    })
  }

  it should "downcast Component when getting it from entity" in {
    val entity = Entity()
    class CustomComponent extends Component
    val customComponent = mock[CustomComponent]

    entity.components.append(customComponent)

    assert(entity.components.length == 1)

    assert(entity.getComponent[CustomComponent] match {
      case Some(cc: CustomComponent) => true
      case _ => false
    })
  }

  it should "die when killed" in {
    val entity = Entity()
    val component = mock[Component]
    entity.components += component
    assert(entity.alive == true)
    assert(entity.components.length == 1)

    entity.kill()
    assert(entity.alive == false)
    assert(entity.components.length == 0)
  }

  it should "throw a DeadEntityException when it is dead" in {
    import crane.exceptions.DeadEntityException
    val entity = Entity()

    entity.kill()

    intercept[DeadEntityException] {
      entity.kill()
    }

    intercept[DeadEntityException] {
      entity.removeComponent[Component]
    }
  }
}

