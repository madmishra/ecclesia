scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/dom-attr", "scbase/loader!dojo/dom", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!extn/components/shipment/backroompick/UpdateHoldLocationExtnUI", "scbase/loader!sc/plat/dojo/utils/EventUtils"],
	function (
		_dojodeclare, _domattr, _dojodom, _scScreenUtils,
		_extnUpdateHoldLocationExtnUI, _EventUtils
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
			stopEnterEvent: function (event , bEvent , ctrl , args) {
				_EventUtils.stopEvent(bEvent);
			}
		});
	});