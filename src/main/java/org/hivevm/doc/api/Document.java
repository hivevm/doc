// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api;

import java.util.Collection;

/**
 * The {@link Document} interface.
 */
public interface Document extends DocumentNode {

    String getId(String link);

    String getTitle();

    Collection<StructuralElement> elements();
}
