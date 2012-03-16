package  
{
	import flash.display.Sprite;
	import flash.display.BitmapData;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.Rectangle;
	import flash.geom.Point;
	import flash.text.TextField;
	import flash.utils.getTimer;
	import flash.text.TextFieldAutoSize;
	
	/**
	 * ...
	 * @author 
	 */
	[SWF(width="800", height="500", frameRate="24", backgroundColor="#CCCCCC")]
	public class Test extends Sprite
	{	
		private static const WIDTH:int = 800;
		private static const HEIGHT:int = 500;
		
		[Embed(source='bg1.jpg')]
		private static var bg1_cls:Class;
		private static var _bg1:BitmapData = new BitmapData(WIDTH, HEIGHT, true, 0);
		[Embed(source='bg2.jpg')]
		private static var bg2_cls:Class;
		private static var _bg2:BitmapData = new BitmapData(WIDTH, HEIGHT, true, 0);
		//static
		{
			_bg1.draw(new bg1_cls);
			_bg2.draw(new bg2_cls);
		}
		
		private static var bg:BitmapData = new BitmapData(WIDTH, HEIGHT, true, 0);
		private static const rectScreen:Rectangle = new Rectangle(0, 0, WIDTH, HEIGHT);
		
		private var _threshold:int = 0;
		private var _lastTime:int = 0;
		private const TIME_INTERVAL:int = 50;
		
		private var _tf:TextField = new TextField();
		
		public function Test() 
		{
			trace("start...");
			this.graphics.beginBitmapFill(bg);
			this.graphics.drawRect(0, 0, 800, 500);
			this.graphics.endFill();
			
			_tf.autoSize = TextFieldAutoSize.LEFT;
			_tf.textColor = 0xffff0000;
			_tf.background = true;
			_tf.backgroundColor = 0xffffffff;
			
			drawRule(_threshold);
			this.addEventListener(Event.ENTER_FRAME, onEnterFrame);
		}
		
		private function onEnterFrame(e:Event):void 
		{
			var curTime:int = getTimer();
			if (curTime - _lastTime > TIME_INTERVAL)
			{
				_lastTime = curTime;
				onTimeup();
			}
		}
		
		private function onTimeup():void
		{
			drawRule(_threshold);
			_threshold++;
			if (_threshold > 255)
			{
				_threshold = 0;
			}
		}
		
		private function drawRule(threshold:int):void 
		{
			bg.lock();
			bg.fillRect(rectScreen, 0);
			bg.draw(_bg1);
			rule(_bg2, bg, rectScreen, threshold);
			_tf.text = "threadhold : " + _threshold;
			bg.draw(_tf);
			bg.unlock();
		}
		
		private function test1():void 
		{
			bg.lock();
			bg.fillRect(new Rectangle(0, 0, 100, 100), 0xffff0000);
			bg.unlock();
		}
		
		
		[Embed(source='rule.png')]
		private static var rule_cls:Class;
		private static var _rule:BitmapData = new BitmapData(800, 500, false, 0);
		//static
		{
			_rule.draw(new rule_cls);
		}
		
		public static function rule(srcBmd:BitmapData, dstBmd:BitmapData, rect:Rectangle, threshold:int):void
		{
			var tempBmd:BitmapData = srcBmd.clone();
			tempBmd.threshold(_rule, rect, new Point(rect.x, rect.y), ">=", threshold, 0, 0xFF, false);
			dstBmd.draw(tempBmd, null, null, null, rect);
			tempBmd.dispose();
		}
	}
}
