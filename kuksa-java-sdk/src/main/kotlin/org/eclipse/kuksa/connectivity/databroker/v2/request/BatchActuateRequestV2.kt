/*
 * Copyright (c) 2023 - 2025 Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package org.eclipse.kuksa.connectivity.databroker.v2.request

import org.eclipse.kuksa.proto.v2.Types.SignalID
import org.eclipse.kuksa.proto.v2.Types.Value

/**
 * Used for batch actuate requests with
 * [org.eclipse.kuksa.connectivity.databroker.DataBrokerConnection.kuksaValV2.batchActuate].
 */
data class BatchActuateRequestV2(val signalIds: List<SignalID>, val value: Value) {
    companion object {
        fun fromVssPaths(vssPaths: List<String>, value: Value): BatchActuateRequestV2 {
            val signalIds = vssPaths.map { vssPath -> SignalID.newBuilder().setPath(vssPath).build() }
            return BatchActuateRequestV2(signalIds, value)
        }

        fun fromIds(ids: List<Int>, value: Value): BatchActuateRequestV2 {
            val signalIds = ids.map { id -> SignalID.newBuilder().setId(id).build() }
            return BatchActuateRequestV2(signalIds, value)
        }
    }
}
