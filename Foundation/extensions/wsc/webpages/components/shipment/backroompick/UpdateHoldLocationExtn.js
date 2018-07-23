scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/dom-attr", "scbase/loader!dojo/dom", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!extn/components/shipment/backroompick/UpdateHoldLocationExtnUI", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!ias/utils/UIUtils"],
	function (
		_dojodeclare, _domattr, _dojodom, _scScreenUtils,
		_extnUpdateHoldLocationExtnUI, _EventUtils, _iasUIUtils
	) {
		return _dojodeclare("extn.components.shipment.backroompick.UpdateHoldLocationExtn", [_extnUpdateHoldLocationExtnUI], {
			// custom code here
			// custom code here
			gettingConfirmed: function () {
				console.log('Got confirmed');
			},
			imGettingCalled: function () {
				console.log('im here');
			},
			isAnyLocPresent: function (holdObj) {
				for (var i = 0; i < holdObj.length; i++) {
					if (holdObj[i].toDelete === "N")
						return true;
				}
				return false
			},
			verifyIfLocationAssigned: function () {
				var self = this;
				var inputBoxRef = this.getWidgetByUId("holdLocationTxtField");
				var inputBox = dijit.byId(inputBoxRef.id);
				var HoldLocationModel = _scScreenUtils.getModel(this, "HoldLocationModel");
				console.log('HoldLocationModel', HoldLocationModel);
				if (HoldLocationModel.HoldLocationList && (HoldLocationModel.HoldLocationList.HoldLocation.length === 1 && self.isAnyLocPresent(HoldLocationModel.HoldLocationList.HoldLocation))) {
					inputBox.set("disabled", true);
				} else {
					inputBox.set("disabled", false);
				}
			},
			test: function () {
				var inputBoxRef = this.getWidgetByUId("holdLocationTxtField");
				var inputBox = dijit.byId(inputBoxRef.id);
				inputBox.set("disabled", true);
			},
			stopEnterEvent: function (event, bEvent, ctrl, args) {
				_EventUtils.stopEvent(bEvent);
			},
			actionConfirm: function () {
				var shipmentModel = _scScreenUtils.getModel(this, "ShipmentModel");
				debugger

				console.log('shipmentModel', shipmentModel);

				var summaryPageInput = {
					Shipment: {
						ShipmentKey: shipmentModel.Shipment.ShipmentKey
					}
				};
				var baseDropStatus = "1100.70.300";

				var shipmentLineArray = shipmentModel.Shipment.ShipmentLines.ShipmentLine;
				for (var i = 0; i < shipmentLineArray.length; i++)
					if (shipmentLineArray[i].OrderLine.GiftWrap === "Y") {
						baseDropStatus = "1100.70.200";
						break;
					}
				var changeShipmentInput = {
					Shipment: {
						ShipmentKey: shipmentModel.Shipment.ShipmentKey,
						BaseDropStatus: baseDropStatus,
						TransactionId: "PRINT_ORDER_SLIP.0001.ex"
					}
				};
				_iasUIUtils.callApi(this, changeShipmentInput, "extn_printStatus", null);
				//_iasUIUtils.openWizardInEditor("wsc.components.shipment.summary.ShipmentSummaryWizard", summaryPageInput, "wsc.desktop.editors.ShipmentEditor", this, null);


			}
		});
	});