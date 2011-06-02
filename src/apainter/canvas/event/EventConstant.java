package apainter.canvas.event;

/**
 * 定数
 * @author nodamushi
 *
 */
public class EventConstant {

	public static final int

		ID_APainterStart =10
		,ID_APainterInited=20
		,ID_APainterEnd=30
		,ID_APainterSend=40
		,ID_CreateCanvas = 50
		,ID_CanvasInited=60
		,ID_DisposeCanvas=70
		/**
		 * ユーザーのペイントが始まった（クリックした）ことを示す。
		 */
		 ,ID_PaintStart = 100
		/**
		 * ユーザーがペイントしていることを示す
		 */
		,ID_Paint = 110
		/**
		 * ユーザーがペイントを終えた（マウスを放した）
		 */
		,ID_PaintEnd = 120

		,ID_CreateLayer=200
		,ID_DeleteLayer=220
		,ID_CreateGroup=250
		,ID_DeleteGroup=270

		,ID_ChangeSelectLayer=300


		;










	private EventConstant() {}
}
