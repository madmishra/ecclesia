scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!dojo/dom", "scbase/loader!extn/components/shipment/summary/ShipmentSummaryShipmentLineDetailsExtnUI"],
	function (
		_dojodeclare, _iasUIUtils, _scScreenUtils, _scWidgetUtils, _dojodom,
		_extnShipmentSummaryShipmentLineDetailsExtnUI
	) {
		return _dojodeclare("extn.components.shipment.summary.ShipmentSummaryShipmentLineDetailsExtn", [_extnShipmentSummaryShipmentLineDetailsExtnUI], {
			// custom code here
			updateItemDetails: function () {
				parentObj = _scScreenUtils.getModel(this, "shipmentLine_Src");
				console.log('parentObj', parentObj);
				var itemKey = parentObj.ShipmentLine.OrderLine.ItemDetails.ItemKey;
				var itemModel = {
					Item: {
						ItemKey: itemKey
					}
				}
				_iasUIUtils.callApi(this, itemModel, "extn_itemDetails", null);
			},
			checkIfValExists: function (event, bEvent, ctrl, args) {
				var self = this;
				if (event) {
					_scWidgetUtils.showWidget(self, ctrl, true, null);
					return event;
				}
			}
		});
	});