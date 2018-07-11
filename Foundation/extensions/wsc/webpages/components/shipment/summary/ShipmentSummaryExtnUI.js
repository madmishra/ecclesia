scDefine(["dojo/text!./templates/ShipmentSummaryExtn.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel" , "scbase/loader!sc/plat/dojo/utils/ScreenUtils"], function (
	templateText,
	_dijitButton,
	_dojodeclare,
	_dojokernel,
	_dojolang,
	_dojotext,
	_idxContentPane,
	_scplat,
	_scButtonDataBinder,
	_scCurrencyDataBinder,
	_scBaseUtils,
	_scDataLabel,
	_scScreenUtils
) {
	return _dojodeclare("extn.components.shipment.summary.ShipmentSummaryExtnUI", [], {
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
						methodName: "printButtonCheck"


					}
				},
				{
					eventId: 'afterScreenLoad'

						,
					sequence: '52'




						,
					handler: {
						methodName: "continueGiftWrap"


					}
				},
				{
					eventId: 'extn_button_onClick'

						,
					sequence: '51'




						,
					handler: {
						methodName: "changePrintStatus"


					}
				},
				{
					eventId: 'showNextPage'

						,
					sequence: '51'




						,
					handler: {
						methodName: "imGettingCalled"


					}
				},
				{
					eventId: 'extn_button1_onClick'

						,
					sequence: '51'




						,
					handler: {
						methodName: "getData"


					}
				},
				{
					eventId: 'extn_button_gift_wrap_onClick'

						,
					sequence: '51'




						,
					handler: {
						methodName: "openConfirmationPopup"


					}
				}

			],
			global: [

				{
					eventId: 'updateSecondaryContact'

						,
					sequence: '51'




						,
					handler: {
						methodName: "updateSecondaryContact"


					}
				}

			]
		},
		handleMashupOutput: function (
			mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
			if (
				_scBaseUtils.equals(
					mashupRefId, "unpack_getNumReasonCodes_refid")) {
				this.handle_unpack_getNumReasonCodes_refid(
					mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "unpack_deleteContainer_refid")) {
				this.handle_unpack_deleteContainer_refid(
					mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "getShipmentLineList")) {
				if (!(
						_scBaseUtils.equals(
							false, applySetModel))) {
					_scScreenUtils.setModel(
						this, "getShipmentLineList_output", modelOutput, null);
				}
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "getShipmentContainerList")) {
				if (!(
						_scBaseUtils.equals(
							false, applySetModel))) {
					_scScreenUtils.setModel(
						this, "getShipmentContainerList_output", modelOutput, null);
				}
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "getShipmentMoreDetails")) {
				this.handleAdditionalInformation(
					modelOutput);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "containerPack_StorePackSlip_94")) {
				this.handle_containerPack_StorePackSlip_94(
					mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "containerPack_StoreLabelReprint_94")) {
				this.handle_containerPack_StoreLabelReprint_94(
					mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
			}
			if (
				_scBaseUtils.equals(
					mashupRefId, "getShipmentDetailsForRecordCustomerPick")) {
				this.handle_getShipmentDetailsForRecordCustomerPick(
					mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
			}
			if(mashupRefId === "extn_addSecondaryContact"){
				this.callLegacyMessage(modelOutput);
			}
		}

	});
});