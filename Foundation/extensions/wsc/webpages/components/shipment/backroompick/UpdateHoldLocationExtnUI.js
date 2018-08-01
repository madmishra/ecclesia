scDefine(["dojo/text!./templates/UpdateHoldLocationExtn.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/form/TextBox", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/SimpleDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!wsc/mobile/home/utils/MobileHomeUtils"
], function (
	templateText,
	_dijitButton,
	_dojodeclare,
	_dojokernel,
	_dojolang,
	_dojotext,
	_idxTextBox,
	_scplat,
	_scButtonDataBinder,
	_scSimpleDataBinder,
	_scBaseUtils,
	_wscMobileHomeUtils
) {
	return _dojodeclare("extn.components.shipment.backroompick.UpdateHoldLocationExtnUI", [], {
		templateString: templateText








			,
		hotKeys: []

			,
		events: []

			,
		subscribers: {

			local: [

				{
					eventId: 'deleteHoldLocation'

						,
					sequence: '51'




						,
					handler: {
						methodName: "verifyIfLocationAssigned"


					}
				},
				{
					eventId: 'holdLocationTxtField_onKeyUp'

						,
					sequence: '19'




						,
					handler: {
						methodName: "stopEnterEvent"


					}
				},
				{
					eventId: 'afterScreenLoad'

						,
					sequence: '51'




						,
					handler: {
						methodName: "verifyIfLocationAssigned"


					}
				},
				{
					eventId: 'extn_datetextbox_onBlur'

						,
					sequence: '19'




						,
					handler: {
						methodName: "addHoldLocationOnEnter"


					}
				},
				{
					eventId: 'actionConfirm'





						,
					handler: {
						methodName: "actionConfirm"


					}
				}

			]
		},
		handleMashupOutput: function (
			mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
			var self = this;
			console.log('modelOutput', modelOutput);
			if (
				_scBaseUtils.equals(
					mashupRefId, "saveHoldLocation")) {
				this.repaintHoldLocationList(
					modelOutput);
				if (modelOutput.Shipment.HoldLocation) {
					var inputBoxRef = self.getWidgetByUId("holdLocationTxtField");
					var inputBox = dijit.byId(inputBoxRef.id);
					inputBox.set("disabled", true);
				}
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "getHoldLocation")) {
				_scScreenUtils.setModel(
					this, "ShipmentModel", modelOutput, null);
				this.repaintHoldLocationList(
					modelOutput);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "extn_printStatus")) {
				_wscMobileHomeUtils.openScreen("extn.mobile.custom.sort.SortScan", "wsc.mobile.editors.MobileEditor");
			}

		}

	});
});