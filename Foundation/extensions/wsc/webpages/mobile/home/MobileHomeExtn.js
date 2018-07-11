
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!wsc/mobile/home/utils/MobileHomeUtils", "scbase/loader!extn/mobile/home/MobileHomeExtnUI", "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils"]
	,
	function (
		_dojodeclare, _scScreenUtils, _scBaseUtils
		, _wscMobileHomeUtils,
		_extnMobileHomeExtnUI, _scWidgetUtils, _scEditorUtils
	) {
		return _dojodeclare("extn.mobile.home.MobileHomeExtn", [_extnMobileHomeExtnUI], {
			// custom code here
			openReadyForGiftWrap: function () {
				_wscMobileHomeUtils.openScreen("extn.mobile.custom.search.SearchResult", "wsc.mobile.editors.MobileEditor");
			},
			test: function () {
				batchModel = _scScreenUtils.getModel(this, "extn_getShipmentListCount_gw");
				console.log('batchModel', batchModel);
			},
			getTheCount: function (dataValue, screen, widget, nameSpace, shipmentModel) {

				if (
					_scBaseUtils.equals("0", dataValue)) {
					_scWidgetUtils.addClass(
						this, "extn_contentpane_gift", "zeroCount");
				}
				return dataValue;
			},

			refreshPageAfterGiftWrap: function (event, bEvent, ctrl, args) {
				var editorInstance = _scEditorUtils.getCurrentEditor();
				console.log('Whos there'); 
				_scEditorUtils.refresh(editorInstance);
			},
			movetoSort: function () {
				_wscMobileHomeUtils.openScreen("extn.mobile.custom.sort.SortScan", "wsc.mobile.editors.MobileEditor");
			},
		});
	});

