package org.intellij.j2ee.web.resin;

import com.intellij.util.xmlb.Accessor;
import com.intellij.util.xmlb.SerializationFilter;
import java.util.Collection;
import java.util.Map;

/**
 * Replaces the platform's internal {@code SkipDefaultsSerializationFilter}.
 * Only serializes values that differ from their type's default, keeping the
 * stored XML free of noise.
 */
final class ResinSerializationFilter implements SerializationFilter {
   @Override
   public boolean accepts(Accessor accessor, Object bean) {
      Object value = accessor.read(bean);
      if (value == null) {
         return false;
      }
      if (value instanceof Boolean) {
         return (Boolean)value;
      }
      if (value instanceof Number) {
         return ((Number)value).doubleValue() != 0;
      }
      if (value instanceof CharSequence) {
         return ((CharSequence)value).length() > 0;
      }
      if (value instanceof Collection) {
         return !((Collection)value).isEmpty();
      }
      if (value instanceof Map) {
         return !((Map)value).isEmpty();
      }
      return true;
   }
}
