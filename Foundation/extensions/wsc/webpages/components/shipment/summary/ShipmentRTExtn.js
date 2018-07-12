scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/dom-style", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!extn/components/shipment/summary/ShipmentRTExtnUI", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!ias/utils/RelatedTaskUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils"],
	function (
		_dojodeclare, domStyle, _scScreenUtils,
		_extnShipmentRTExtnUI, _iasUIUtils, _scBaseUtils, _iasRelatedTaskUtils, _scModelUtils
	) {
		return _dojodeclare("extn.components.shipment.summary.ShipmentRTExtn", [_extnShipmentRTExtnUI], {
			// custom code here
			hideRTScreen: function () {
				var rtRef = dijit.byId(this.id);
				rtModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");

			},
			hideOrShowRelatedTasks: function (
				event, bEvent, ctrl, args) {
				var shipmentDetailsModel = null;
				shipmentDetailsModel = _scBaseUtils.getAttributeValue("shipmentModel", false, args);
				var deliveryMethod = null;
				deliveryMethod = _scModelUtils.getStringValueFromPath("Shipment.DeliveryMethod", shipmentDetailsModel);
				var statusPath = null;
				statusPath = _scBaseUtils.getAttributeValue("statusPath", false, args);
				var status = null;
				status = _scModelUtils.getStringValueFromPath(
					statusPath, shipmentDetailsModel);
				if (
					_scBaseUtils.equals(
						deliveryMethod, "SHP")) {
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_PrintPickTicket", false);
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_RecordPickShipment", false);
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_AssignToHold", false);
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_PackShipment", false);
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_PrintPackSlip", false);
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_UnpackShipment", false);
					if (
						_scBaseUtils.contains(
							status, "1100.70.06.10")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_PrintPickTicket", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_RecordPickShipment", false);
					} else if (
						_scBaseUtils.contains(
							status, "1100.70.06.20")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_PrintPickTicket", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_RecordPickShipment", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_AssignToHold", false);
					} else if (
						_scBaseUtils.contains(
							status, "1100.70.06.50")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_AssignToHold", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_PackShipment", false);
					} else if (
						_scBaseUtils.contains(
							status, "1100.70.06.70")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_PackShipment", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_UnpackShipment", false);
					} else if (
						_scBaseUtils.contains(
							status, "1100.70.200")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_AssignToHold", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "extn_link", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "extn_link1", false);
					} else if (
						_scBaseUtils.contains(
							status, "1300")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_PrintPackSlip", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_UnpackShipment", false);
					} else if (
						_scBaseUtils.contains(
							status, "1400")) { }
				} else if (
					_scBaseUtils.equals(
						deliveryMethod, "PICK")) {
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_PrintPickTicket", false);
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_RecordPickShipment", false);
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_AssignToHold", false);
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_RecordCustomerPick", false);
					_iasRelatedTaskUtils.showTaskInWebAndMobile(
						this, "lnk_RT_PrintReceipt", false);
					if (
						_scBaseUtils.contains(
							status, "1100.70.06.10")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_PrintPickTicket", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_RecordPickShipment", false);
					} else if (
						_scBaseUtils.contains(
							status, "1100.70.06.20")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_PrintPickTicket", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_RecordPickShipment", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_AssignToHold", false);
					} else if (
						_scBaseUtils.contains(
							status, "1100.70.06.30")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_AssignToHold", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_RecordCustomerPick", false);
					} else if (
						_scBaseUtils.contains(
							status, "1100.70.200")) {

						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "extn_link", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "extn_link1", false);
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_AssignToHold", false);
					} else if (
						_scBaseUtils.contains(
							status, "1400")) {
						_iasRelatedTaskUtils.enableTaskInWebAndMobile(
							this, "lnk_RT_PrintReceipt", false);
					}
				}
				if (!(
					this.hasPermissionForPrintPickTicket(
						shipmentDetailsModel))) {
					_iasRelatedTaskUtils.hideTaskInWebAndMobile(
						this, "lnk_RT_PrintPickTicket", false);
				}
				if (!(
					this.hasPermissionForBackroomPick(
						shipmentDetailsModel))) {
					_iasRelatedTaskUtils.hideTaskInWebAndMobile(
						this, "lnk_RT_RecordPickShipment", false);
				}
			},
			openAssignPickup: function (event, bEvent, ctrl, args) {
				_iasUIUtils.openSimplePopup("extn.customScreen.popup.SecondaryPickPerson", "Secondary pick up person", this, null, null);
			},
			invokePrint: function (event, bEvent, ctrl, args) {
				var Batchmodel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
				console.log('Invoking print with the input', Batchmodel);
				if (window.webkit) {
					window.webkit.messageHandlers.invokePrint.postMessage(batchModel);
				}
			}
		});
	});