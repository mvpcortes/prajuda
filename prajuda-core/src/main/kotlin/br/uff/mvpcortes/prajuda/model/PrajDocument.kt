package br.uff.mvpcortes.prajuda.model

import br.uff.mvpcortes.prajuda.model.validation.RelativePath

data class PrajDocument (
        override var id: String? = null,
        var content: String = "",
        var tag: String = "",
        /**
         * path to file in repository
         */
        @field:RelativePath var path: String = "",

        /**
         * A document can  have not service (orphan?)
         */
        var serviceId: String? = null,
        /**
         * We save the serviceName to avoid make a foreign key that. It is redundant and we know it.
         */
        var serviceName: String? = ""): WithId{

}