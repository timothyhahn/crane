package crane

/** The base System class - must override process **/
abstract class System {
 def process(delta: Int)
 var world: World = null
}
