scDefine(["dojo/text!./templates/ShipmentRTExtn.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/Link"], function (
	templateText,
	_dojodeclare,
	_dojokernel,
	_dojolang,
	_dojotext,
	_idxContentPane,
	_scplat,
	_scCurrencyDataBinder,
	_scBaseUtils,
	_scLink
) {
	return _dojodeclare("extn.components.shipment.summary.ShipmentRTExtnUI", [], {
		templateString: templateText








			,
		hotKeys: []

			,
		events: []

			,
		subscribers: {

			local: [

				{
					eventId: 'hideOrShowRelatedTasks'

						,
					sequence: '51'




						,
					handler: {
						methodName: "hideRTScreen"


					}
				},
				{
					eventId: 'extn_link_onClick'

						,
					sequence: '51'




						,
					handler: {
						methodName: "openAssignPickup"


					}
				},
				{
					eventId: 'extn_link1_onClick'

						,
					sequence: '51'




						,
					handler: {
						methodName: "openAssignPickup"


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
		}

	});
});