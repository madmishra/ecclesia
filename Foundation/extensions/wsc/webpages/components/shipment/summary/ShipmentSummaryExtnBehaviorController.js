


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/components/shipment/summary/ShipmentSummaryExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnShipmentSummaryExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.components.shipment.summary.ShipmentSummaryExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.components.shipment.summary.ShipmentSummaryExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_printStatus'
,
		 mashupId : 			'extn_changeShipmentStatus'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_addSecondaryContact'
,
		 mashupId : 			'addSecondaryContact'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_callLegacyMessage'
,
		 mashupId : 			'callLegacyMessage'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_getPrintDetails'
,
		 mashupId : 			'extn_getPrintDetails'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

