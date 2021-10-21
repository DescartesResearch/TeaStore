{{- define "teastore.image.microservice" -}}
image
{{- end }}

{{- define "teastore.image.fullname" -}}
{{- include "teastore.fullname" . }}-{{- include "teastore.image.microservice" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "teastore.image.selectorLabels" -}}
{{ include "teastore.selectorLabels" . }}
app.kubernetes.io/name: {{ include "teastore.image.microservice" . }}
app.kubernetes.io/component: imagegenerator
{{- end }}

{{- define "teastore.image.hostname" -}}
{{- if .Values.clientside_loadbalancer -}}
$(POD_NAME).{{- .Values.image.svc_name -}}
{{- else if .Values.image.url -}}
{{- .Values.image.url -}}
{{- else -}}
{{- .Values.image.svc_name -}}
{{- end -}}
{{- end -}}
