scDefine(["dojo/text!./templates/BackroomPickProductScanExtn.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils"], function (
	templateText,
	_dojodeclare,
	_dojokernel,
	_dojolang,
	_dojotext,
	_scplat,
	_scBaseUtils,
	_scScreenUtils
) {
	return _dojodeclare("extn.components.shipment.backroompick.BackroomPickProductScanExtnUI", [], {
		templateString: templateText








			,
		hotKeys: []

			,
		events: []

			,
		subscribers: {

			local: [

				{
					eventId: 'afterScreenLoad'

						,
					sequence: '51'




						,
					handler: {
						methodName: "startScannerInApp"


					}
				}

			]
		},
		handleMashupOutput: function (
			mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
			if (
				_scBaseUtils.equals(
					mashupRefId, "getNotPickedShipmentLineListCount")) {
				this.showHideShipmentLineList(
					modelOutput);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "validateChangeShipmentStatusOnNext")) {
				this.canProceedToNextScreen(
					modelOutput);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "saveShipmentStatusForPickUpOrder")) {
				this.gotoNextScreen();
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "saveShipmentStatusForShipOrder")) {
				this.gotoNextScreen();
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "getNotPickedShipmentLineListOnNext")) {
				this.isPickComplete(
					modelOutput);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "getAllShipmentLineList")) {
				this.updateShipmentLineListPanel(
					modelOutput, "ALLLINES");
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "getNotPickedShipmentLineList")) {
				this.updateShipmentLineListPanel(
					modelOutput, "SHORTLINES");
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "updateShipmentQuantityForPickAllLine")) {
				console.log('modelOutput', modelOutput);
				this.refreshShipmentLineAfterQuantityUpdate(
					modelOutput);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "updateShipmentQuantityForPickAll")) {
				console.log('modelOutput', modelOutput1);
				this.showConfirmMsgBoxAfterPickAll(
					modelOutput);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "translateBarCode")) {
				if (!(
						_scBaseUtils.equals(
							false, applySetModel))) {
					_scScreenUtils.setModel(
						this, "translateBarCode_output", modelOutput, null);
				}
				this.updateProductQuantity(
					modelOutput);
			}
			if (mashupRefId === "extn_getPrintDetails") {
				this.printDetails(modelOutput);
			}
		}

	});
});