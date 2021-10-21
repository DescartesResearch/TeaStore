{{- define "teastore.persistence.microservice" -}}
persistence
{{- end }}

{{- define "teastore.persistence.fullname" -}}
{{- include "teastore.fullname" . }}-{{- include "teastore.persistence.microservice" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "teastore.persistence.selectorLabels" -}}
{{ include "teastore.selectorLabels" . }}
app.kubernetes.io/name: {{ include "teastore.persistence.microservice" . }}
app.kubernetes.io/component: cache
{{- end }}

{{- define "teastore.persistence.hostname" -}}
{{- if .Values.clientside_loadbalancer -}}
$(POD_NAME).{{- .Values.persistence.svc_name -}}
{{- else if .Values.persistence.url -}}
{{- .Values.persistence.url -}}
{{- else -}}
{{- .Values.persistence.svc_name -}}
{{- end -}}
{{- end -}}
