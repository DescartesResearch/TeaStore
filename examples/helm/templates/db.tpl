{{- define "teastore.db.microservice" -}}
db
{{- end }}

{{- define "teastore.db.fullname" -}}
{{- include "teastore.fullname" . }}-{{- include "teastore.db.microservice" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "teastore.db.selectorLabels" -}}
{{ include "teastore.selectorLabels" . }}
app.kubernetes.io/name: {{ include "teastore.db.microservice" . }}
app.kubernetes.io/component: database
{{- end }}

{{- define "teastore.db.url" -}}
{{- if .Values.db.url -}}
{{ .Values.db.url}}
{{- else -}}
{{ .Values.db.svc_name }}
{{- end -}}
{{- end -}}
