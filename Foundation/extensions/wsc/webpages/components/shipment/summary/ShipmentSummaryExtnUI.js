
scDefine(["dojo/text!./templates/ShipmentSummaryExtn.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel"]
	, function (
		templateText
		,
		_dijitButton
		,
		_dojodeclare
		,
		_dojokernel
		,
		_dojolang
		,
		_dojotext
		,
		_idxContentPane
		,
		_scplat
		,
		_scButtonDataBinder
		,
		_scCurrencyDataBinder
		,
		_scBaseUtils
		,
		_scDataLabel
	) {
		return _dojodeclare("extn.components.shipment.summary.ShipmentSummaryExtnUI",
			[], {
				templateString: templateText








				,
				hotKeys: [
				]

				, events: [
				]

				, subscribers: {

					local: [

						{
							eventId: 'afterScreenLoad'

							, sequence: '51'




							, handler: {
								methodName: "printButtonCheck"


							}
						}
						,
						{
							eventId: 'afterScreenLoad'

							, sequence: '52'




							, handler: {
								methodName: "continueGiftWrap"


							}
						}
						,
						{
							eventId: 'extn_button_onClick'

							, sequence: '51'




							, handler: {
								methodName: "changePrintStatus"


							}
						}
						,
						{
							eventId: 'showNextPage'

							, sequence: '51'




							, handler: {
								methodName: "imGettingCalled"


							}
						}
						,
						{
							eventId: 'extn_button1_onClick'

							, sequence: '51'




							, handler: {
								methodName: "getData"


							}
						}, {
							eventId: 'extn_button_gift_wrap_onClick'

							, sequence: '51'




							, handler: {
								methodName: "openConfirmationPopup"


							}
						}

					]
				},
				handleMashupOutput: function (
					mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
					if (mashupRefId === "extn_printStatus") {
						this.refreshPage();
					}
				}

			});
	});


