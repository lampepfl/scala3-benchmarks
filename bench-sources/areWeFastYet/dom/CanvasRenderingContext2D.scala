package dom

trait CanvasRenderingContext2D:
  var fillStyle: String
  def fillRect(x: Int, y: Int, w: Int, h: Int): Unit
