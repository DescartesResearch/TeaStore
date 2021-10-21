{{- define "teastore.registry.microservice" -}}
registry
{{- end }}

{{- define "teastore.registry.fullname" -}}
{{- include "teastore.fullname" . }}-{{- include "teastore.registry.microservice" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "teastore.registry.selectorLabels" -}}
{{ include "teastore.selectorLabels" . }}
app.kubernetes.io/name: {{ include "teastore.registry.microservice" . }}
app.kubernetes.io/component: registry
{{- end }}

{{- define "teastore.registry.url" -}}
{{- if .Values.registry.url -}}
{{ .Values.registry.url}}
{{- else -}}
{{ .Values.registry.svc_name }}
{{- end -}}
{{- end -}}
