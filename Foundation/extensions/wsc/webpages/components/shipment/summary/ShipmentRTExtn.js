scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/dom-style", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!extn/components/shipment/summary/ShipmentRTExtnUI",  "scbase/loader!ias/utils/UIUtils"],
	function (
		_dojodeclare, domStyle, _scScreenUtils,
		_extnShipmentRTExtnUI, _iasUIUtils
	) {
		return _dojodeclare("extn.components.shipment.summary.ShipmentRTExtn", [_extnShipmentRTExtnUI], {
			// custom code here
			hideRTScreen: function () {
				var rtRef = dijit.byId(this.id);
				rtModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
				if (rtModel.Shipment && rtModel.Shipment.Status === "1100.70.200") {
					domStyle.set(rtRef.domNode, 'display', 'none');

				}
			},
			openAssignPickup: function (event, bEvent, ctrl, args) {
				_iasUIUtils.openSimplePopup("extn.customScreen.popup.SecondaryPickPerson", "Secondary pick up person", this, null, null);
			},
			updateSecondaryContact: function (event, bEvent, ctrl, args) {
				console.log('event', event.SecondaryPerson);
			}
		});
	});