


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/components/shipment/summary/ShipmentSummaryExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnShipmentSummaryExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.components.shipment.summary.ShipmentSummaryExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.components.shipment.summary.ShipmentSummaryExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'getShipmentDetails_output'
,
		 mashupRefId : 			'getShipmentDetails'
,
		 sequence : 			''
,
		 mashupId : 			'shipSummary_getShipmentDetails'
,
		 callSequence : 			''
,
		 extnType : 			'ADD'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

